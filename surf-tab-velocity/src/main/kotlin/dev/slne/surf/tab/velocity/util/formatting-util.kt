package dev.slne.surf.tab.velocity.util

import com.velocitypowered.api.proxy.Player
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.tab.api.entry.TabGroup
import dev.slne.surf.tab.velocity.plugin
import io.github.miniplaceholders.api.MiniPlaceholders
import io.github.miniplaceholders.api.types.RelationalAudience
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.jvm.optionals.getOrNull

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")


fun String.formatWithAdventure(player: Player, other: Audience? = null): Component {
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
        .replaceText {
            it.matchLiteral("<server>")
            it.replacement(buildText {
                info(
                    player.currentServer.getOrNull()?.server?.serverInfo?.name
                        ?: "N/A"
                )
            })
        }
        .replaceText {
            it.matchLiteral("<players_online>")
            it.replacement(buildText {
                info(
                    plugin.proxy.allPlayers.size
                )
            })
        }
        .replaceText {
            it.matchLiteral("<players_max>")
            it.replacement(buildText {
                info(
                    plugin.proxy.configuration.showMaxPlayers
                )
            })
        }
        .replaceText {
            it.matchLiteral("<date>")
            it.replacement(buildText {
                info(ZonedDateTime.now().format(dateFormatter))
            })
        }
        .replaceText {
            it.matchLiteral("<time>")
            it.replacement(buildText {
                info(ZonedDateTime.now().format(timeFormatter))
            })
        }
}

fun TextColor.mm() = "<${this.asHexString()}>"

fun TabGroup.getServers() = clients.mapNotNull { plugin.proxy.getServer(it).getOrNull() }