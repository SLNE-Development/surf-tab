package dev.slne.surf.tab.core.client.service

import dev.slne.surf.tab.core.client.platform.TabViewer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

/**
 * Turns a configured template into the component a player is shown.
 */
interface TablistRenderer {

    /**
     * The resolvers that fill in what the tablist knows about, for the whole of one update.
     */
    fun placeholders(values: TablistValues): TagResolver

    /**
     * Renders [template] with [placeholders].
     *
     * [player] is the audience placeholders are resolved against, or `null` when the template does
     * not mention anything that could differ per audience and one render is shared by everybody.
     */
    fun render(template: String, player: TabViewer?, placeholders: TagResolver): Component
}
