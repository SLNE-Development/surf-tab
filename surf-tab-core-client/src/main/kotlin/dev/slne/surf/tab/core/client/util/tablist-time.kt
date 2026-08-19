package dev.slne.surf.tab.core.client.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * The date [dateTime] falls on, as the tablist spells it out.
 */
fun formatTablistDate(dateTime: ZonedDateTime): String = dateTime.format(dateFormatter)

/**
 * The time of day [dateTime] falls on, as the tablist spells it out.
 */
fun formatTablistTime(dateTime: ZonedDateTime): String = dateTime.format(timeFormatter)
