package dev.slne.surf.tab.core.client.service

import dev.slne.surf.tab.core.client.config.tablistConfig
import dev.slne.surf.tab.core.client.platform.TabPlatform
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toKotlinDuration

object TablistScheduler {

    /**
     * How long after a minute begins the clock is looked at.
     *
     * Waking a moment late means the new minute is definitely there. Waking a moment early would
     * mean rendering the minute that just ended and showing it for a whole minute.
     */
    private val PAST_THE_MINUTE = 100.milliseconds

    /**
     * How long the loop sleeps when nothing in the configuration changes on its own.
     *
     * Nothing is updated when it wakes up; it only looks at whether the configuration has been
     * reloaded into something that does need a timer after all.
     */
    private val IDLE = 1.minutes

    @Volatile
    private var task: Job? = null

    fun startTask() {
        task = TabPlatform.launch { run() }
    }

    fun cancelTask() {
        task?.cancel()
        task = null
    }

    private suspend fun run() {
        while (currentCoroutineContext().isActive) {
            val tick = nextTick(
                TablistService.templates(),
                ZonedDateTime.now(),
                tablistConfig.unknownPlaceholderRefreshSeconds.seconds
            )

            delay(tick.delay)
            tick.reason?.let { TablistService.invalidateAll(it) }
        }
    }

    /**
     * When the loop should wake up next, and what it should ask for when it does.
     *
     * @param delay how long to sleep
     * @param reason why to update afterward, or `null` when there is nothing to do
     */
    class Tick(val delay: Duration, val reason: TablistUpdateReason?)

    /**
     * The next moment [templates] can render differently without anything happening.
     */
    fun nextTick(
        templates: TablistTemplates,
        now: ZonedDateTime,
        fallbackInterval: Duration
    ): Tick {
        val clock = if (templates.usesClock) untilNextMinute(now) else null
        val fallback = if (templates.rendersPerPlayer) fallbackInterval else null

        return when {
            clock != null && (fallback == null || clock <= fallback) -> Tick(
                clock,
                TablistUpdateReason.CLOCK
            )

            fallback != null -> Tick(fallback, TablistUpdateReason.UNKNOWN_PLACEHOLDERS)
            else -> Tick(IDLE, null)
        }
    }

    /**
     * How long it is until the clock reads the next minute.
     */
    private fun untilNextMinute(now: ZonedDateTime): Duration {
        val nextMinute = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)
        return java.time.Duration.between(now, nextMinute).toKotlinDuration() + PAST_THE_MINUTE
    }
}
