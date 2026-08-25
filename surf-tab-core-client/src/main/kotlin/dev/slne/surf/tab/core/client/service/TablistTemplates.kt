package dev.slne.surf.tab.core.client.service

import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * The configured header and footer, analysed together.
 *
 * @param header the template shown above the player list
 * @param footer the template shown below the player list
 */
class TablistTemplates(val header: TablistTemplate, val footer: TablistTemplate) {

    /**
     * Whether either template mentions [placeholder].
     */
    fun uses(placeholder: TablistPlaceholder): Boolean {
        return header.dependsOn(placeholder) || footer.dependsOn(placeholder)
    }

    /**
     * Whether either template mentions the date or the time, and therefore has to be rendered again
     * when the clock moves on.
     */
    val usesClock
        get() = uses(TablistPlaceholder.DATE) || uses(TablistPlaceholder.TIME)

    /**
     * Whether either template has to be rendered for every player separately.
     */
    val rendersPerPlayer
        get() = header.rendersPerPlayer || footer.rendersPerPlayer

    /**
     * Whether an update for [reason] can change what either template renders as.
     */
    fun affectedBy(reason: TablistUpdateReason) = when (reason) {
        TablistUpdateReason.PLAYER_COUNT -> uses(TablistPlaceholder.PLAYERS_ONLINE)
        TablistUpdateReason.CLOCK -> usesClock
        TablistUpdateReason.UNKNOWN_PLACEHOLDERS -> rendersPerPlayer
        TablistUpdateReason.CONFIGURATION -> true
    }

    /**
     * Whether these are the analysed form of exactly [header] and [footer].
     */
    fun matches(header: String, footer: String): Boolean {
        return this.header.source == header && this.footer.source == footer
    }

    override fun toString() = "TablistTemplates(header=$header, footer=$footer)"

    companion object {

        /**
         * Analyses [header] and [footer] with [miniMessage].
         */
        fun analyze(header: String, footer: String, miniMessage: MiniMessage) = TablistTemplates(
            TablistTemplate.analyze(header, miniMessage),
            TablistTemplate.analyze(footer, miniMessage)
        )
    }
}
