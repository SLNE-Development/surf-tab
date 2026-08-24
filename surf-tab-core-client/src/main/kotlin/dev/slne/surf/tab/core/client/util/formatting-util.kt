package dev.slne.surf.tab.core.client.util

import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.text
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.core.api.common.server.SurfServer
import dev.slne.surf.tab.core.client.platform.TabPlatform
import dev.slne.surf.tab.core.client.platform.TabPlayer
import io.github.miniplaceholders.api.MiniPlaceholders
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag.selfClosingInserting
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.resolver
import java.time.ZonedDateTime

private val globalResolver = resolver(
    MiniPlaceholders.globalPlaceholders(),
    MiniPlaceholders.audiencePlaceholders(),
    MiniPlaceholders.relationalPlaceholders(),
    MiniPlaceholders.relationalGlobalPlaceholders(),
    MiniPlaceholders.audienceGlobalPlaceholders(),
    resolver(
        "server",
        selfClosingInserting(text(SurfServer.current().name, Colors.VARIABLE_VALUE))
    ),
)

fun tablistPlaceholders(onlinePlayerCount: Int): TagResolver {
    val now = ZonedDateTime.now()

    return resolver(
        globalResolver,
        resolver(
            "players_online",
            selfClosingInserting(text(onlinePlayerCount, Colors.INFO))
        ),
        resolver(
            "players_max",
            selfClosingInserting(text(TabPlatform.maxPlayerCount(), Colors.INFO))
        ),
        resolver(
            "date",
            selfClosingInserting(text(formatTablistDate(now), Colors.INFO))
        ),
        resolver(
            "time",
            selfClosingInserting(text(formatTablistTime(now), Colors.INFO))
        ),
    )
}

fun String.formatWithAdventure(player: TabPlayer, placeholders: TagResolver): Component =
    miniMessage.deserialize(this, player.audience, placeholders)
