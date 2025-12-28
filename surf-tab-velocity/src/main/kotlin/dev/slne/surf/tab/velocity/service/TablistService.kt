package dev.slne.surf.tab.velocity.service

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.api.entry.TabGameMode
import dev.slne.surf.tab.velocity.hook.LuckPermsHook
import dev.slne.surf.tab.velocity.tablistConfig
import dev.slne.surf.tab.velocity.util.formatWithAdventure
import dev.slne.surf.tab.velocity.util.getServers
import dev.slne.surf.tab.velocity.util.toTabProfile
import dev.slne.surf.tab.velocity.util.toVelocity
import java.util.*
import kotlin.jvm.optionals.getOrNull

val tablistService = VelocityTablistService()

class VelocityTablistService {
    val entries = mutableObject2ObjectMapOf<RegisteredServer, List<TabEntry>>()

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
        val currentServer = player.currentServer.getOrNull()?.server ?: return
        entries[currentServer]?.forEach { entry ->
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

    fun updateOthersFor(viewer: Player) {
        val server = viewer.currentServer.getOrNull()?.server ?: return

        val seenServers = tablistService.getSeenServers(server)
        val seenPlayers = seenServers.flatMap { it.playersConnected }.distinct()
            .filterNot { it.uniqueId == viewer.uniqueId }

        seenPlayers.forEach { seen ->
            tablistService.removePlayer(viewer, seen.uniqueId)
            tablistService.addPlayer(viewer, tablistService.createEntry(seen))
        }

        // As velocity has no chat session api, we cannot update the tablist entries properly yet. Currently, it will cause the chat validation to fail.
        // [00:21:47] [Render thread/ERROR]: Received chat message from 1c779cb1-3860-4e23-9cac-7f160b2acc61, but they have no chat session initialized and secure chat is enforced
    }

    fun updateForOthers(toUpdate: Player) {
        val server = toUpdate.currentServer.getOrNull()?.server ?: return

        val seenServers = tablistService.getSeenServers(server)
        val seenPlayers = seenServers.flatMap { it.playersConnected }.distinct()
            .filterNot { it.uniqueId == toUpdate.uniqueId }

        seenPlayers.forEach { viewer ->
            tablistService.removePlayer(viewer, toUpdate.uniqueId)
            tablistService.addPlayer(viewer, tablistService.createEntry(toUpdate))
        }
    }
}