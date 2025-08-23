package dev.slne.surf.tab.velocity.util

import io.github.miniplaceholders.api.MiniPlaceholders
import io.github.miniplaceholders.api.types.RelationalAudience
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver


fun String.formatWithAdventure(player: Audience, other: Audience? = null): Component {
    val resolvers = listOf(
        MiniPlaceholders.globalPlaceholders(),
        MiniPlaceholders.audiencePlaceholders(),
        MiniPlaceholders.relationalPlaceholders(),
        MiniPlaceholders.relationalGlobalPlaceholders(),
        MiniPlaceholders.audienceGlobalPlaceholders()
    )

    if (other != null) {
        return MiniMessage.miniMessage()
            .deserialize(this, RelationalAudience(player, other), TagResolver.resolver(resolvers))
    }

    return MiniMessage.miniMessage().deserialize(this, player, TagResolver.resolver(resolvers))
}

fun TextColor.mm() = "<${this.asHexString()}>"
