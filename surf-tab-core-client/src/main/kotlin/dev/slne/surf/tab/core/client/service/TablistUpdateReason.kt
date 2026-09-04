package dev.slne.surf.tab.core.client.service

import dev.slne.surf.tab.api.placeholder.TabPlaceholder

/**
 * Describes why the tab list header and footer should be considered for re-rendering.
 *
 * Update reasons allow the renderer to skip work when the active templates cannot be affected by the
 * reported change.
 */
sealed interface TablistUpdateReason {

    /**
     * Indicates that the values of the specified [placeholders] may have changed.
     *
     * @property placeholders the placeholders whose values should be considered invalid
     */
    data class Placeholders(val placeholders: Set<TabPlaceholder>) : TablistUpdateReason

    /**
     * Indicates that values which cannot be observed through registered placeholder update conditions
     * may have changed.
     */
    data object UnknownPlaceholders : TablistUpdateReason

    /**
     * Indicates that the template structure or its available placeholders may have changed.
     *
     * This is used after configuration reloads and changes to the set of registered placeholders.
     */
    data object Configuration : TablistUpdateReason

    companion object {

        /**
         * Indicates that the wall clock has advanced to a new minute.
         *
         * Both date and time placeholders are included because crossing a minute boundary may also
         * cross into a new day.
         */
        val CLOCK: TablistUpdateReason =
            Placeholders(setOf(BuiltinPlaceholder.DATE, BuiltinPlaceholder.TIME))

        /**
         * Indicates that the number of currently online players may have changed.
         */
        val PLAYER_COUNT: TablistUpdateReason =
            Placeholders(setOf(BuiltinPlaceholder.PLAYERS_ONLINE))
    }
}
