package dev.slne.surf.tab.core.client.service

import dev.slne.surf.tab.api.placeholder.TabPlaceholder
import net.kyori.adventure.text.Component

/**
 * Immutable snapshot of placeholder values captured for a single tab list update.
 *
 * Each referenced placeholder is evaluated at most once when the snapshot is created, ensuring that
 * all templates rendered from this snapshot observe the same value. [generation] provides an ordering
 * between snapshots created by different updates.
 *
 * @property generation monotonically increasing identifier of this snapshot
 * @property onlinePlayers the number of players online when this snapshot was captured
 */
class TablistValues(
    val generation: Long,
    private val values: Map<TabPlaceholder, Component>,
    val onlinePlayers: Int
) {

    /**
     * The placeholders for which this snapshot contains a successfully captured value.
     */
    val placeholders: Set<TabPlaceholder> get() = values.keys

    /**
     * Returns the value captured for [placeholder].
     *
     * @param placeholder the placeholder whose captured value should be returned
     * @return the captured component, or `null` if no value is present in this snapshot
     */
    fun read(placeholder: TabPlaceholder): Component? = values[placeholder]

    /**
     * Determines whether [placeholder] has a different captured value than in [previous].
     *
     * Missing values are compared as `null`, so a placeholder becoming available or unavailable is
     * also considered a change.
     *
     * @param placeholder the placeholder to compare
     * @param previous the snapshot to compare against
     * @return `true` if the captured values differ, otherwise `false`
     */
    fun changed(placeholder: TabPlaceholder, previous: TablistValues): Boolean {
        return read(placeholder) != previous.read(placeholder)
    }

    override fun toString(): String {
        return "TablistValues(" +
                "generation=$generation, " +
                "onlinePlayers=$onlinePlayers, " +
                "values=${values.keys.map { it.tagName }}" +
                ")"
    }
}
