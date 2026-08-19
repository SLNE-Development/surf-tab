package dev.slne.surf.tab.core.client.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class TablistTimeTest {

    private val dateTime = ZonedDateTime.of(2024, 3, 7, 9, 5, 42, 0, ZoneId.of("Europe/Berlin"))

    @Test
    fun `a date is written day first and zero padded`() {
        assertEquals("07.03.2024", formatTablistDate(dateTime))
    }

    @Test
    fun `a time is written in 24 hours without seconds`() {
        assertEquals("09:05", formatTablistTime(dateTime))
    }

    @Test
    fun `an afternoon time keeps counting past twelve`() {
        assertEquals("17:45", formatTablistTime(dateTime.withHour(17).withMinute(45)))
    }

    @Test
    fun `a date is written in the zone it carries`() {
        val newYork = dateTime.withHour(1).withZoneSameInstant(ZoneId.of("America/New_York"))

        assertEquals("06.03.2024", formatTablistDate(newYork))
        assertEquals("19:05", formatTablistTime(newYork))
    }
}
