package dev.slne.surf.tab.server.placeholder.impl

import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.player.CloudPlayerManager
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.tab.server.placeholder.type.ContextualPlaceholderExtension
import dev.slne.surf.tab.server.placeholder.type.PlaceholderExtension
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object CloudPlaceholderExtension : PlaceholderExtension,
    ContextualPlaceholderExtension<CloudPlayer> {

    override val name = "cloud"

    private val dateFormatter = DateTimeFormatter.ofPattern("dd:MM:yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    override fun resolver(): TagResolver {
        return TagResolver.resolver(
            Placeholder.parsed(
                "cloud_online_players",
                CloudPlayerManager.getOnlinePlayers().size.toString()
            ),
            Placeholder.parsed(
                "cloud_max_players",
                CloudServer.all().sumOf { it.maxPlayerCount }.toString()
            ),
            Placeholder.parsed("cloud_time", LocalDateTime.now().format(timeFormatter)),
            Placeholder.parsed("cloud_date", LocalDateTime.now().format(dateFormatter))
        )
    }

    override fun resolver(context: CloudPlayer): TagResolver {
        return TagResolver.resolver(
            Placeholder.parsed("cloud_server", context.currentServer().name),
            Placeholder.parsed("cloud_name", context.name),
            Placeholder.component("cloud_afk", parseAfkTag(context.isAfk()))
        )
    }

    private fun parseAfkTag(isAfk: Boolean): Component {
        return if (isAfk) buildText {
            darkSpacer("[")
            spacer("AFK")
            darkSpacer("]")
        } else Component.empty()
    }
}
