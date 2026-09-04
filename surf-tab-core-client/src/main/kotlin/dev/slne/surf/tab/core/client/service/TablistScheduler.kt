package dev.slne.surf.tab.core.client.service

import dev.slne.surf.tab.api.placeholder.TabPlaceholder
import dev.slne.surf.tab.api.placeholder.UpdateCondition
import dev.slne.surf.tab.core.client.platform.tabPlatform
import dev.slne.surf.tab.core.client.service.TablistUpdateReason
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toKotlinDuration

/**
 * Schedules tab list refreshes for content that can change without an observable external event.
 *
 * Refreshes are scheduled for templates using the clock, placeholders with an
 * [UpdateCondition.Every] condition, and per-player templates containing placeholders whose changes
 * cannot otherwise be observed.
 *
 * The scheduler always waits for the earliest applicable refresh condition. When no configured
 * template requires periodic updates, it wakes periodically without invalidating anything so that
 * configuration changes can take effect without restarting the scheduler.
 */
object TablistScheduler {

    /**
     * Additional delay after the start of a minute before clock-based templates are invalidated.
     *
     * This ensures that the newly started minute is already observable when templates are rendered.
     */
    private val PAST_THE_MINUTE = 100.milliseconds

    /**
     * Polling interval used when no configured template currently requires periodic invalidation.
     *
     * An idle wake-up does not invalidate any templates; it only recalculates the next required tick.
     */
    private val IDLE = 1.minutes

    @Volatile
    private var task: Job? = null

    /**
     * Starts the scheduler task.
     */
    fun startTask() {
        task = tabPlatform.launch { run() }
    }

    /**
     * Cancels the current scheduler task, if one is running.
     */
    fun cancelTask() {
        task?.cancel()
        task = null
    }

    private suspend fun run() {
        while (currentCoroutineContext().isActive) {
            val tick = nextTick(
                TablistService.templates(),
                ZonedDateTime.now(),
                TablistService.refreshInterval()
            )

            delay(tick.delay)
            tick.reason?.let { TablistService.invalidateAll(it) }
        }
    }

    /**
     * Describes the next scheduled scheduler wake-up.
     *
     * @property delay the duration to wait before the scheduler wakes up
     * @property reason the reason to invalidate the tab list after waking, or `null` when the
     * scheduler should only recalculate its next tick
     */
    class Tick(val delay: Duration, val reason: TablistUpdateReason?)

    /**
     * Determines the earliest point at which [templates] may need to be rendered again without an
     * explicit external invalidation.
     *
     * Clock-based templates are scheduled for the beginning of the next minute, periodic
     * placeholders are scheduled according to the shortest [UpdateCondition.Every] interval among
     * all referenced placeholders, and per-player templates are refreshed after [fallbackInterval]
     * to account for values whose changes cannot be observed directly.
     *
     * When multiple conditions apply, the earliest one determines the returned tick. All periodic
     * placeholders are invalidated together at their shortest requested interval, so placeholders
     * with longer intervals may be evaluated more frequently than requested.
     *
     * If no periodic refresh is required, an idle tick is returned without an invalidation reason.
     *
     * @param templates the currently active tab list templates
     * @param now the current time used to calculate the next clock boundary
     * @param fallbackInterval the refresh interval for per-player templates with unobservable values
     * @return the next scheduler tick
     */
    fun nextTick(
        templates: TablistTemplates,
        now: ZonedDateTime,
        fallbackInterval: Duration
    ): Tick {
        val ticks = ObjectArrayList<Tick>(3)

        if (templates.usesClock) {
            ticks += Tick(untilNextMinute(now), TablistUpdateReason.CLOCK)
        }

        val periodic = templates.placeholders.filter { it.interval() != null }
        periodic.minOfOrNull { it.interval()!! }?.let { shortest ->
            ticks += Tick(shortest, TablistUpdateReason.Placeholders(periodic.toSet()))
        }

        if (templates.rendersPerPlayer) {
            ticks += Tick(fallbackInterval, TablistUpdateReason.UnknownPlaceholders)
        }

        return ticks.minByOrNull { it.delay } ?: Tick(IDLE, null)
    }

    /**
     * Returns the shortest periodic update interval configured for this placeholder.
     *
     * @return the shortest [UpdateCondition.Every] interval, or `null` if this placeholder has no
     * periodic update condition
     */
    private fun TabPlaceholder.interval(): Duration? {
        return UpdateCondition.flatten(updates)
            .filterIsInstance<UpdateCondition.Every>()
            .minOfOrNull { it.interval }
    }

    /**
     * Calculates the duration until shortly after the beginning of the next minute.
     *
     * @param now the current time
     * @return the duration until the next minute boundary plus [PAST_THE_MINUTE]
     */
    private fun untilNextMinute(now: ZonedDateTime): Duration {
        val nextMinute = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)
        return java.time.Duration.between(now, nextMinute).toKotlinDuration() + PAST_THE_MINUTE
    }
}
