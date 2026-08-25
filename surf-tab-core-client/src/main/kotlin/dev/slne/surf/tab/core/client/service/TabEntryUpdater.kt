package dev.slne.surf.tab.core.client.service

import dev.slne.surf.api.core.messages.adventure.buildText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.text.Component
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

enum class TabEntryPart {
    ORDER,
    VANISH,
    CLAN,
    LIVE,
    AFK
}

/**
 * Builds a player's tablist entry in two stages.
 *
 * The base entry is shown before the suspendable clan enrichment starts. Failures in one optional
 * part fall back only that part, while cancellation still propagates to the update coalescer.
 */
class TabEntryUpdater<T>(
    private val baseName: suspend (T) -> Component,
    private val order: (T) -> Int,
    private val vanishTag: (T) -> Component,
    private val clanTag: suspend (T) -> Component?,
    private val liveTag: (T) -> Component,
    private val afkTag: (T) -> Component,
    private val show: suspend (T, Component, Int) -> Unit,
    private val clanTimeout: Duration = 2.seconds,
    private val onPartFailure: (TabEntryPart, T, Throwable) -> Unit
) {

    suspend fun update(target: T) {
        val base = baseName(target)
        val order = resolve(TabEntryPart.ORDER, target, 0) { order(target) }
        val vanish = resolve(TabEntryPart.VANISH, target, Component.empty()) { vanishTag(target) }
        val live = resolve(TabEntryPart.LIVE, target, Component.empty()) { liveTag(target) }
        val afk = resolve(TabEntryPart.AFK, target, Component.empty()) { afkTag(target) }

        show(target, render(base, vanish, null, live, afk), order)

        val clan = resolveClan(target) ?: return
        show(target, render(base, vanish, clan, live, afk), order)
    }

    private suspend fun resolveClan(target: T): Component? = try {
        withTimeout(clanTimeout) {
            clanTag(target)
        }
    } catch (timeout: TimeoutCancellationException) {
        onPartFailure(TabEntryPart.CLAN, target, timeout)
        null
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        onPartFailure(TabEntryPart.CLAN, target, throwable)
        null
    }

    private fun render(
        base: Component,
        vanish: Component,
        clan: Component?,
        live: Component,
        afk: Component
    ) = buildText {
        append(vanish)
        append(base)
        clan?.let { append(it) }
        append(live)
        append(afk)
    }

    private suspend fun <R> resolve(
        part: TabEntryPart,
        target: T,
        fallback: R,
        value: suspend () -> R
    ): R = try {
        value()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (throwable: Throwable) {
        onPartFailure(part, target, throwable)
        fallback
    }
}
