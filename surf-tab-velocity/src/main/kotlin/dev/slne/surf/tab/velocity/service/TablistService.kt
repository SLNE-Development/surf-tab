package dev.slne.surf.tab.velocity.service

import com.velocitypowered.api.proxy.Player
import dev.slne.clan.api.clan.Clan
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.tab.velocity.hook.LuckPermsHook
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.tablistConfig
import dev.slne.surf.tab.velocity.util.formatWithAdventure
import net.kyori.adventure.text.Component
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

    fun isAfk(playerUuid: UUID) = plugin.afkPlayers.contains(playerUuid)
    fun isVanished(playerUuid: UUID) = plugin.vanishedPlayers.contains(playerUuid)


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

    private suspend fun formatDisplayName(playerUuid: UUID, name: String) = buildText {
        append(getVanishTag(playerUuid))
        append(
            MiniMessage.miniMessage().deserialize(
                LuckPermsHook.getPrefix(playerUuid) + name + LuckPermsHook.getSuffix(playerUuid)
            )
        )
        appendSpace()
        append(getClanTag(playerUuid))
        append(getAfkTag(playerUuid))
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


    private suspend fun getClanTag(playerUuid: UUID) =
        if (plugin.proxy.pluginManager.isLoaded("surf-clan-velocity")) Clan.byPlayer(playerUuid)
            ?.renderClanTag(10) ?: Component.empty() else Component.empty()
}