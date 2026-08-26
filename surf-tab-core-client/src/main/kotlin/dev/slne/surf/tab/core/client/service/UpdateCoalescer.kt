package dev.slne.surf.tab.core.client.service

import kotlinx.coroutines.withTimeoutOrNull
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Coalesces update requests so that at most one update is running for a key at any time.
 *
 * Requests that arrive while an update is already running do not start another concurrent update.
 * Instead, they replace the pending request for that key. Once the current update finishes, the
 * loop performs one additional pass using the target from the most recent request.
 *
 * This is particularly useful for updates that may be invalidated frequently or in bursts. A
 * tablist entry, for example, can be invalidated by permission recalculations, clan changes, AFK or
 * vanish state changes, Redis updates, and entity tracking. Starting a complete rebuild for every
 * individual invalidation would repeat the same work unnecessarily, whereas coalescing reduces an
 * arbitrary number of overlapping requests to at most one pending follow-up pass.
 *
 * Updates for the same key are also serialized for correctness. If two updates were allowed to run
 * concurrently, both could observe different snapshots of the underlying state and apply their
 * results in the opposite order. Serial execution guarantees that a follow-up update only starts
 * after the previous result has been fully applied.
 *
 * The target is not fixed for the lifetime of an update loop. Every request supplies a target, and
 * the latest target replaces any previously pending one. This is important when a key can refer to
 * a different object over time, such as a player disconnecting and reconnecting while an update is
 * suspended. In that case, the follow-up pass operates on the new player object rather than the
 * object that originally started the loop.
 *
 * Requests for different keys are independent and may be processed concurrently according to the
 * scheduling behavior of [runUpdates].
 *
 * @param runUpdates schedules an update loop on the scope or dispatcher on which updates must run
 * @param updateTimeout the maximum time one update pass may occupy its key
 * @param onTimeout observes a pass that exceeded [updateTimeout]
 * @param update performs a single update for the supplied target
 */
internal class UpdateCoalescer<T>(
    private val runUpdates: (suspend () -> Unit) -> Unit,
    private val updateTimeout: Duration = 5.seconds,
    private val onTimeout: (UUID, T) -> Unit = { _, _ -> },
    private val update: suspend (T) -> Unit
) {

    init {
        require(updateTimeout.isPositive()) {
            "updateTimeout must be positive but was $updateTimeout"
        }
    }

    /**
     * Stores the current request for every key with an active update loop.
     *
     * The value initially represents the request whose target is being processed. If another request
     * arrives while that update is running, the map entry is atomically replaced with a new
     * [Requested] instance. The running loop detects that replacement after finishing its current
     * pass and continues with the replacement's target.
     *
     * Entries exist only while an update loop is active. The loop removes its entry before
     * completing, so inactive keys are not retained indefinitely.
     *
     * Request identity is significant: replacing an entry with a different [Requested] instance is
     * how the running loop detects that another update was requested, even when both requests carry
     * the same target.
     */
    private val inFlight = ConcurrentHashMap<UUID, Requested<T>>()

    /**
     * Requests an update of [target] for [key].
     *
     * If no update loop is currently active for the key, this request starts one. Otherwise, it
     * replaces the currently pending request so that the active loop performs a follow-up pass using
     * this target. Multiple requests arriving before that follow-up begins are therefore collapsed
     * to the most recent target.
     *
     * This method is safe to call concurrently from any thread.
     *
     * @param key identifies the independently serialized update stream
     * @param target the target that should be used by the newest requested update
     */
    fun request(key: UUID, target: T) {
        while (true) {
            val running = inFlight[key]

            if (running == null) {
                val started = Requested(target)

                // Nothing was running, so this call owns the update loop - unless another thread
                // claimed it first, in which case the retry hands the target to that loop instead.
                if (inFlight.putIfAbsent(key, started) == null) {
                    return handOver(key, started)
                }

                continue
            }

            // Replace rather than put: if the loop finished in between, this fails and the retry
            // starts a fresh loop, instead of leaving behind a target that nobody would pick up.
            if (inFlight.replace(key, running, Requested(target))) return
        }
    }

    /**
     * Schedules the update loop for [key].
     *
     * If [runUpdates] fails before accepting the loop, the entry installed by this request is
     * removed again. Without that cleanup the key would remain marked as active even though no loop
     * exists to process it, causing subsequent requests to be silently absorbed.
     *
     * @param key the key whose update loop is being started
     * @param started the request that installed the initial in-flight entry
     */
    private fun handOver(key: UUID, started: Requested<T>) {
        try {
            runUpdates { drain(key, started) }
        } catch (throwable: Throwable) {
            inFlight.remove(key, started)
            throw throwable
        }
    }

    /**
     * Processes updates for [key] until no newer request remains.
     *
     * Each pass operates on the target contained in the request currently owned by the loop. After
     * the pass completes, the loop attempts to remove that exact request from [inFlight]. Successful
     * removal means no request arrived while the update was running and the loop can terminate.
     *
     * If removal fails, another request replaced the entry in the meantime. The loop then continues
     * with that newer request, thereby coalescing all intervening requests into the latest target.
     */
    private suspend fun drain(key: UUID, started: Requested<T>) {
        var current = started

        while (true) {
            try {
                val completed = withTimeoutOrNull(updateTimeout) {
                    update(current.target)
                    true
                }

                if (completed == null) {
                    runCatching {
                        onTimeout(key, current.target)
                    }
                }
            } catch (throwable: Throwable) {
                giveUp(key, current)
                throw throwable
            }

            // Nothing was requested while the pass ran, so the loop is finished. A failed removal
            // means a request replaced the entry, and the next pass continues from that entry -
            // which is how a target that changed mid-loop is the one that ends up applied.
            if (inFlight.remove(key, current)) return

            current = inFlight.getValue(key)
        }
    }

    /**
     * Cleans up an update loop after its current pass failed.
     *
     * The in-flight entry must be removed so that the key cannot remain permanently marked as busy.
     * If a newer request arrived while the failed pass was running, that request belongs to neither
     * the failed operation nor its target and is therefore submitted again as a fresh update loop.
     *
     * @param key the key whose update failed
     * @param current the request whose update threw
     */
    private fun giveUp(key: UUID, current: Requested<T>) {
        while (true) {
            val requested = inFlight[key] ?: return

            if (requested === current) {
                if (inFlight.remove(key, current)) return
                continue
            }

            return handOver(key, requested)
        }
    }
}

/**
 * Represents one update request and its target.
 *
 * Instances intentionally use reference identity rather than value equality. [UpdateCoalescer]
 * detects a newer request by observing that the [Requested] instance stored for a key has changed.
 * Consequently, two requests must remain distinguishable even when they contain the same target.
 */
private class Requested<T>(val target: T)
