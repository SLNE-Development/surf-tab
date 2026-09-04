package dev.slne.surf.tab.core.client.util

import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.tab.core.client.platform.TabViewer
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
 * Every known placeholder the update snapshotted, filled in as [values] read them, plus everything
 * MiniPlaceholders contributes.
 */
fun tablistPlaceholders(values: TablistValues): TagResolver {
    val resolvers = TagResolver.builder().resolver(miniPlaceholders)

    for (placeholder in values.placeholders) {
        val value = values.read(placeholder) ?: continue
        resolvers.tag(placeholder.tagName, selfClosingInserting(value))
    }

    return resolvers.build()
}

fun String.formatWithAdventure(
    player: TabViewer,
    placeholders: TagResolver
): Component = miniMessage.deserialize(this, player.audience, placeholders)

fun String.formatWithAdventure(
    placeholders: TagResolver
): Component = miniMessage.deserialize(this, placeholders)

/**
 * Renders templates with Adventure MiniMessage.
 */
object AdventureTablistRenderer : TablistRenderer {

    override fun placeholders(values: TablistValues) = tablistPlaceholders(values)

    override fun render(template: String, player: TabViewer?, placeholders: TagResolver) =
        if (player == null) {
            template.formatWithAdventure(placeholders)
        } else {
            template.formatWithAdventure(player, placeholders)
        }
}
