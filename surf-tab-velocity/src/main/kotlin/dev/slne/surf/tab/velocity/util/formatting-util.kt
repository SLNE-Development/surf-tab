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
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.jvm.optionals.getOrNull

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val miniMessage = MiniMessage.miniMessage()
private val globalResolver = TagResolver.resolver(
    MiniPlaceholders.globalPlaceholders(),
    MiniPlaceholders.audiencePlaceholders(),
    MiniPlaceholders.relationalPlaceholders(),
    MiniPlaceholders.relationalGlobalPlaceholders(),
    MiniPlaceholders.audienceGlobalPlaceholders()
)

private fun dynamicResolver(player: Player): TagResolver =
    TagResolver.resolver(
        TagResolver.resolver("server") { _, _ ->
            val name = player.currentServer
                .getOrNull()?.server?.serverInfo?.name ?: "N/A"
            Tag.inserting {
                buildText { info(name) }
            }
        },
        TagResolver.resolver("players_online") { _, _ ->
            Tag.inserting {
                buildText { info(plugin.proxy.allPlayers.size) }
            }
        },
        TagResolver.resolver("players_max") { _, _ ->
            Tag.inserting {
                buildText { info(plugin.proxy.configuration.showMaxPlayers) }
            }
        },
        TagResolver.resolver("date") { _, _ ->
            Tag.inserting {
                buildText { info(nowDate()) }
            }
        },
        TagResolver.resolver("time") { _, _ ->
            Tag.inserting {
                buildText { info(nowTime()) }
            }
        }
    )


fun String.formatWithAdventure(player: Player, other: Audience? = null): Component {
    if (other != null) {
        return MiniMessage.miniMessage()
            .deserialize(
                this,
                RelationalAudience(player, other),
                TagResolver.resolver(globalResolver)
            )
    }

    return MiniMessage.miniMessage().deserialize(this, player, TagResolver.resolver(globalResolver))
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

private fun nowDate(): String {
    updateTimeCache()
    return cachedDate
}

private fun nowTime(): String {
    updateTimeCache()
    return cachedTime
}


fun TextColor.mm() = "<${this.asHexString()}>"

fun TabGroup.getServers() = clients.mapNotNull { plugin.proxy.getServer(it).getOrNull() }