package dev.slne.surf.tab.core.client.util

import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.text
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.tab.core.client.platform.TabPlayer
import dev.slne.surf.tab.core.client.service.TablistPlaceholder
import dev.slne.surf.tab.core.client.service.TablistRenderer
import dev.slne.surf.tab.core.client.service.TablistValues
import io.github.miniplaceholders.api.MiniPlaceholders
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag.selfClosingInserting
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.resolver

/**
 * Everything MiniPlaceholders contributes.
 */
private val miniPlaceholders = resolver(
    MiniPlaceholders.globalPlaceholders(),
    MiniPlaceholders.audiencePlaceholders(),
    MiniPlaceholders.relationalPlaceholders(),
    MiniPlaceholders.relationalGlobalPlaceholders(),
    MiniPlaceholders.audienceGlobalPlaceholders()
)

/**
 * All placeholders that are used in tablist templates.
 */
fun tablistPlaceholders(values: TablistValues): TagResolver = resolver(
    miniPlaceholders,
    placeholder(TablistPlaceholder.SERVER, text(values.server, Colors.VARIABLE_VALUE)),
    placeholder(TablistPlaceholder.PLAYERS_ONLINE, text(values.onlinePlayers, Colors.INFO)),
    placeholder(TablistPlaceholder.PLAYERS_MAX, text(values.maxPlayers, Colors.INFO)),
    placeholder(TablistPlaceholder.DATE, text(values.date, Colors.INFO)),
    placeholder(TablistPlaceholder.TIME, text(values.time, Colors.INFO))
)

private fun placeholder(
    placeholder: TablistPlaceholder,
    value: Component
) = resolver(placeholder.tagName, selfClosingInserting(value))

fun String.formatWithAdventure(
    player: TabPlayer,
    placeholders: TagResolver
): Component = miniMessage.deserialize(this, player.audience, placeholders)

fun String.formatWithAdventure(
    placeholders: TagResolver
): Component = miniMessage.deserialize(this, placeholders)

/**
 * Renderer implementation for generating and formatting tablist components using Adventure MiniMessage.
 */
object AdventureTablistRenderer : TablistRenderer {

    override fun placeholders(values: TablistValues) = tablistPlaceholders(values)

    override fun render(template: String, player: TabPlayer?, placeholders: TagResolver) =
        if (player == null) {
            template.formatWithAdventure(placeholders)
        } else {
            template.formatWithAdventure(player, placeholders)
        }
}
