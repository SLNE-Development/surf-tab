package dev.slne.surf.tab.velocity.service

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.api.entry.TabGameMode
import dev.slne.surf.tab.velocity.hook.LuckPermsHook
import dev.slne.surf.tab.velocity.tablistConfig
import dev.slne.surf.tab.velocity.util.formatWithAdventure
import dev.slne.surf.tab.velocity.util.getServers
import dev.slne.surf.tab.velocity.util.toTabProfile
import dev.slne.surf.tab.velocity.util.toVelocity
import java.util.*

val tablistService = VelocityTablistService()

class VelocityTablistService {
    val entries = mutableObjectSetOf<TabEntry>()

    fun addPlayer(viewer: Player, entry: TabEntry) {
        viewer.tabList.addEntry(entry.toVelocity(viewer.tabList))
    }

    fun removePlayer(viewer: Player, entryUuid: UUID) {
        viewer.tabList.removeEntry(entryUuid)
    }

    fun sendAdditions(player: Player) {
        player.sendPlayerListHeaderAndFooter(
            tablistConfig.header.formatWithAdventure(player),
            tablistConfig.footer.formatWithAdventure(player)
        )
    }

    fun sendCurrentTablist(player: Player) {
        println("Sending current tablist to player ${player.username}: ${entries.map { it.profile.name }}")
        entries.forEach { entry ->
            addPlayer(player, entry)
        }
    }

    fun getSeenServers(base: RegisteredServer): List<RegisteredServer> {
        val groups = tablistConfig.groups.map { it.toTabGroup() }

        return groups
            .filter { base in it.getServers() }
            .flatMap { it.getServers() }
            .distinct()
    }

    fun createEntry(target: Player) = TabEntry(
        profile = target.gameProfile.toTabProfile(),
        displayName = tablistConfig.nameFormat.formatWithAdventure(target),
        ping = target.ping.toInt(),
        gameMode = TabGameMode.SURVIVAL,
        weight = LuckPermsHook.getWeight(target.uniqueId)
    )

    fun updatePlayerInTablist(player: Player) {
//        val server = player.currentServer.getOrNull()?.server ?: return
//
//        val seenServers = tablistService.getSeenServers(server)
//        val viewers = seenServers.flatMap { it.playersConnected }.distinct()
//
//        viewers.forEach { viewer ->
//            tablistService.removePlayer(viewer, player.uniqueId)
//            tablistService.addPlayer(viewer, tablistService.createEntry(player, viewer))
//        }
//
//        tablistService.removePlayer(player, player.uniqueId)
//        tablistService.addPlayer(player, tablistService.createEntry(player, player))
        // As velocity has no chat session api, we cannot update the tablist entries properly yet. Currently, it will cause the chat validation to fail.
        // [00:21:47] [Render thread/ERROR]: Received chat message from 1c779cb1-3860-4e23-9cac-7f160b2acc61, but they have no chat session initialized and secure chat is enforced
    }
}