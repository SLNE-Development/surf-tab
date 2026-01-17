package dev.slne.surf.tab.velocity.util

import com.velocitypowered.api.proxy.Player
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.tab.api.entry.TabGroup
import dev.slne.surf.tab.velocity.plugin
import io.github.miniplaceholders.api.MiniPlaceholders
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.jvm.optionals.getOrNull

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
                variableValue(
                    player.currentServer.getOrNull()?.server?.serverInfo?.name
                        ?: "N/A"
                )
            })
        }
        .replaceText {
            it.matchLiteral("<players_online>")
            it.replacement(buildText {
                info(
                    plugin.proxy.playerCount
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

@Volatile
private var cachedMinute = -1

@Volatile
private var cachedDate = ""

@Volatile
private var cachedTime = ""

private fun updateTimeCache() {
    val now = ZonedDateTime.now()
    val minute = now.minute
    if (minute != cachedMinute) {
        cachedMinute = minute
        cachedDate = now.format(dateFormatter)
        cachedTime = now.format(timeFormatter)
    }
}

fun TabGroup.getServers() = clients.mapNotNull { plugin.proxy.getServer(it).getOrNull() }