package dev.slne.surf.tab.core.client.service

import net.kyori.adventure.text.minimessage.MiniMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class TablistSchedulerTest {

    private val miniMessage: MiniMessage = MiniMessage.miniMessage()
    private val now: ZonedDateTime =
        ZonedDateTime.of(2024, 3, 7, 9, 5, 42, 0, ZoneId.of("Europe/Berlin"))

    private val fallback = 5.seconds

    private fun templates(header: String, footer: String) =
        TablistTemplates.analyze(header, footer, miniMessage)

    private fun nextTick(header: String, footer: String) =
        TablistScheduler.nextTick(templates(header, footer), now, fallback)

    @Test
    fun `a template with a clock in it wakes up on the next minute`() {
        val tick = nextTick("<time>", "<red>bye")

        assertEquals(TablistUpdateReason.CLOCK, tick.reason)
        assertEquals(18.seconds + 100.milliseconds, tick.delay, "eighteen seconds are left of 09:05")
    }

    @Test
    fun `a date is enough to be put on the clock`() {
        assertEquals(TablistUpdateReason.CLOCK, nextTick("<date>", "<red>bye").reason)
    }

    @Test
    fun `a template nothing can change on its own is not put on a timer`() {
        val tick = nextTick("<players_online>", "<server>")

        assertNull(tick.reason, "the number of players is announced by an event, not by a clock")
    }

    @Test
    fun `a placeholder nobody announces is looked at again on the fallback`() {
        val tick = nextTick("<red>hello", "<player_ping>")

        assertEquals(TablistUpdateReason.UNKNOWN_PLACEHOLDERS, tick.reason)
        assertEquals(fallback, tick.delay)
    }

    @Test
    fun `whichever is due first is what the loop waits for`() {
        val tick = nextTick("<time>", "<player_ping>")

        assertEquals(TablistUpdateReason.UNKNOWN_PLACEHOLDERS, tick.reason)
        assertEquals(fallback, tick.delay, "the fallback is due long before the minute is over")
    }

    @Test
    fun `the minute wins once it is closer than the fallback`() {
        val almostTheMinute = now.withSecond(58)
        val tick = TablistScheduler.nextTick(
            templates("<time>", "<player_ping>"),
            almostTheMinute,
            fallback
        )

        assertEquals(TablistUpdateReason.CLOCK, tick.reason)
        assertEquals(2.seconds + 100.milliseconds, tick.delay)
    }
}
