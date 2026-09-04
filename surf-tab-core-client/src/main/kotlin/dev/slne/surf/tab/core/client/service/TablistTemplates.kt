package dev.slne.surf.tab.core.client.service

import dev.slne.surf.tab.api.placeholder.TabPlaceholder
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * The header and the footer, analysed together.
 *
 * @param header the template shown above the player list
 * @param footer the template shown below the player list
 */
class TablistTemplates(val header: TablistTemplate, val footer: TablistTemplate) {

    /**
     * Whether either template names [placeholder].
     */
    fun uses(placeholder: TabPlaceholder): Boolean {
        return header.dependsOn(placeholder) || footer.dependsOn(placeholder)
    }

    /**
     * Every known placeholder either template names.
     */
    val placeholders: Set<TabPlaceholder>
        get() = header.placeholders + footer.placeholders

    /**
     * Whether either template names the date or the time, and therefore has to be rendered again
     * when the clock moves on.
     */
    val usesClock
        get() = uses(BuiltinPlaceholder.DATE) || uses(BuiltinPlaceholder.TIME)

    /**
     * Whether either template has to be rendered for every player separately.
     */
    val rendersPerPlayer
        get() = header.rendersPerPlayer || footer.rendersPerPlayer

    /**
     * Whether an update for [reason] can change what either template renders as.
     */
    fun affectedBy(reason: TablistUpdateReason) = when (reason) {
        is TablistUpdateReason.Placeholders -> reason.placeholders.any { uses(it) }
        TablistUpdateReason.UnknownPlaceholders -> rendersPerPlayer
        TablistUpdateReason.Configuration -> true
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
         * Analyses [header] and [footer] with [miniMessage], recognising the placeholders [resolve]
         * knows.
         */
        fun analyze(
            header: String,
            footer: String,
            miniMessage: MiniMessage,
            resolve: (String) -> TabPlaceholder? = BuiltinPlaceholder::byTagName
        ) = TablistTemplates(
            TablistTemplate.analyze(header, miniMessage, resolve),
            TablistTemplate.analyze(footer, miniMessage, resolve)
        )
    }
}
