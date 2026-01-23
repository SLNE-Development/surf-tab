package dev.slne.surf.tab.velocity.service

import com.velocitypowered.api.proxy.Player
import dev.slne.surf.tab.velocity.hook.LuckPermsHook
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.tablistConfig
import dev.slne.surf.tab.velocity.util.formatWithAdventure
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.*
import kotlin.jvm.optionals.getOrNull

val tablistService = VelocityTablistService()

class VelocityTablistService {
    fun sendAdditions(player: Player) {
        player.sendPlayerListHeaderAndFooter(
            tablistConfig.header.formatWithAdventure(player),
            tablistConfig.footer.formatWithAdventure(player)
        )
    }


    suspend fun formatOnlinePlayers(viewer: Player) {
        viewer.tabList.entries.forEach { entry ->
            viewer.tabList.addEntry(entry.apply {
                setDisplayName(formatDisplayName(entry.profile.id, entry.profile.name))
                listOrder = LuckPermsHook.getWeight(entry.profile.id)
            })
        }
    }

    suspend fun reformatPlayerForOnlinePlayers(targetPlayerUuid: UUID) {
        plugin.proxy.allPlayers.forEach { viewer ->
            val tablist = viewer.tabList
            val entry = tablist.getEntry(targetPlayerUuid).getOrNull() ?: return@forEach

            tablist.addEntry(entry.apply {
                setDisplayName(formatDisplayName(entry.profile.id, entry.profile.name))
                listOrder = LuckPermsHook.getWeight(entry.profile.id)
            })
        }
    }

    suspend fun formatNewPlayer(viewer: Player, newPlayerUuid: UUID) {
        val tablist = viewer.tabList
        val entry = tablist.getEntry(newPlayerUuid).getOrNull() ?: return

        tablist.addEntry(entry.apply {
            setDisplayName(formatDisplayName(entry.profile.id, entry.profile.name))
            listOrder = LuckPermsHook.getWeight(entry.profile.id)
        })
    }

    fun formatLostPlayer(viewer: Player, lostPlayerUuid: UUID) {
        val tablist = viewer.tabList
        val entry = tablist.getEntry(lostPlayerUuid).getOrNull() ?: return

        tablist.removeEntry(entry.profile.id)
    }

    private suspend fun formatDisplayName(playerUuid: UUID, name: String) =
        MiniMessage.miniMessage()
            .deserialize(
                LuckPermsHook.getPrefix(playerUuid) +
                        name +
                        LuckPermsHook.getSuffix(playerUuid) +
                        " " +
                        getClanTag(playerUuid)
            )

    private fun getClanTag(playerUuid: UUID) = ""
//        if (plugin.proxy.pluginManager.isLoaded("surf-clan-velocity")) MiniMessage.miniMessage()
//            .serialize(
//                surfClanApi.renderClanTag(
//                    playerUuid
//                )
//            ) else ""
}