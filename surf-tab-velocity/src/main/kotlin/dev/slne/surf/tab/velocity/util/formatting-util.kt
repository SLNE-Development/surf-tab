package dev.slne.surf.tab.velocity.util

import io.github.miniplaceholders.api.MiniPlaceholders
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver


fun String.formatWithAdventure(): Component {
    val resolvers = listOf(
        MiniPlaceholders.globalPlaceholders(),
        MiniPlaceholders.audiencePlaceholders(),
        MiniPlaceholders.relationalPlaceholders(),
        MiniPlaceholders.relationalGlobalPlaceholders(),
        MiniPlaceholders.audienceGlobalPlaceholders()
    )

    return MiniMessage.miniMessage().deserialize(this, TagResolver.resolver(resolvers))
}

fun TextColor.mm() = "<${this.asHexString()}>"
