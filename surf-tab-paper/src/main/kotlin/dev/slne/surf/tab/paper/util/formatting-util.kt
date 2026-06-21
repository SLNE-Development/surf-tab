package dev.slne.surf.tab.paper.util

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.core.api.common.server.SurfServer
import io.github.miniplaceholders.api.MiniPlaceholders
import net.kyori.adventure.text.TextReplacementConfig
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
        .replaceText(
            TextReplacementConfig.builder()
                .matchLiteral("<server>")
                .replacement(buildText {
                    variableValue(SurfServer.current().name)
                })
                .build()
        )
        .replaceText(
            TextReplacementConfig.builder()
                .matchLiteral("<players_online>")
                .replacement(buildText {
                    info(Bukkit.getOnlinePlayers().size)
                })
                .build()
        )
        .replaceText(
            TextReplacementConfig.builder()
                .matchLiteral("<players_max>")
                .replacement(buildText {
                    info(Bukkit.getMaxPlayers())
                })
                .build()
        )
        .replaceText(
            TextReplacementConfig.builder()
                .matchLiteral("<date>")
                .replacement(buildText {
                    info(ZonedDateTime.now().format(dateFormatter))
                })
                .build()
        )
        .replaceText(
            TextReplacementConfig.builder()
                .matchLiteral("<time>")
                .replacement(buildText {
                    info(ZonedDateTime.now().format(timeFormatter))
                })
                .build()
        )