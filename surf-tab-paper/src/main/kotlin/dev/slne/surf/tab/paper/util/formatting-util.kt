package dev.slne.surf.tab.paper.util

import dev.slne.surf.core.api.common.server.SurfServer
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.github.miniplaceholders.api.MiniPlaceholders
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val globalResolver = TagResolver.resolver(
    MiniPlaceholders.globalPlaceholders(),
    MiniPlaceholders.audiencePlaceholders(),
    MiniPlaceholders.relationalPlaceholders(),
    MiniPlaceholders.relationalGlobalPlaceholders(),
    MiniPlaceholders.audienceGlobalPlaceholders()
)

fun String.formatWithAdventure(player: Player) =
    MiniMessage.miniMessage().deserialize(this, player, TagResolver.resolver(globalResolver))
        .replaceText {
            it.matchLiteral("<server>")
            it.replacement(buildText {
                variableValue(SurfServer.current().name)
            })
        }
        .replaceText {
            it.matchLiteral("<players_online>")
            it.replacement(buildText {
                info(Bukkit.getOnlinePlayers().size)
            })
        }
        .replaceText {
            it.matchLiteral("<players_max>")
            it.replacement(buildText {
                info(Bukkit.getMaxPlayers())
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