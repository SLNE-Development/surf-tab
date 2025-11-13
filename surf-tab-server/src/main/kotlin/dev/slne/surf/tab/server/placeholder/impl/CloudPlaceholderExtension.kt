package dev.slne.surf.tab.server.placeholder.impl

import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.player.CloudPlayerManager
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.tab.server.placeholder.type.ContextualPlaceholderExtension
import dev.slne.surf.tab.server.placeholder.type.PlaceholderExtension
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CloudPlaceholderExtension : PlaceholderExtension,
    ContextualPlaceholderExtension<CloudPlayer> {
    override val name = "cloud"

    private val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    override fun resolver() = TagResolver.resolver(
        Placeholder.parsed("online_players", CloudPlayerManager.getOnlinePlayers().size.toString()),
        Placeholder.parsed("max_players", CloudServer.all().sumOf { it.maxPlayerCount }.toString()),
        Placeholder.parsed("time", LocalDateTime.now().format(timeFormatter)),
        Placeholder.parsed("date", LocalDateTime.now().format(dateFormatter))
    )

    override fun resolver(context: CloudPlayer) = TagResolver.resolver(
        Placeholder.parsed("server", context.currentServer().name)
    )
}