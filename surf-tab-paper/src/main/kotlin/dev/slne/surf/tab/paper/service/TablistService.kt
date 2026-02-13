package dev.slne.surf.tab.paper.service

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.tab.paper.hook.LuckPermsHook
import dev.slne.surf.tab.paper.redisLoader
import dev.slne.surf.tab.paper.tablistConfig
import dev.slne.surf.tab.paper.util.formatWithAdventure
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import java.util.*

val tablistService = VelocityTablistService()

class VelocityTablistService {
    fun sendAdditions(player: Player) {
        player.sendPlayerListHeaderAndFooter(
            tablistConfig.header.formatWithAdventure(player),
            tablistConfig.footer.formatWithAdventure(player)
        )
    }

    fun isAfk(playerUuid: UUID) = redisLoader.afkPlayers.contains(playerUuid)
    fun isVanished(playerUuid: UUID) = redisLoader.vanishedPlayers.contains(playerUuid)

    fun formatPlayer(player: Player) {
        player.playerListName(formatDisplayName(player))
        player.playerListOrder = LuckPermsHook.getWeight(player.uniqueId)
    }

    private fun formatDisplayName(player: Player) = buildText {
        append(getVanishTag(player.uniqueId))
        append(
            MiniMessage.miniMessage().deserialize(
                LuckPermsHook.getPrefix(player.uniqueId) + player.name + LuckPermsHook.getSuffix(
                    player.uniqueId
                )
            )
        )
        appendSpace()
        append(getAfkTag(player.uniqueId))
    }

    private fun getAfkTag(playerUuid: UUID) = if (isAfk(playerUuid)) {
        buildText {
            appendSpace()
            darkSpacer("[")
            spacer("AFK")
            darkSpacer("]")
        }
    } else {
        Component.empty()
    }

    private fun getVanishTag(playerUuid: UUID) = if (isVanished(playerUuid)) {
        buildText {
            darkSpacer("[")
            note("V")
            darkSpacer("]")
            appendSpace()
        }
    } else {
        Component.empty()
    }
}