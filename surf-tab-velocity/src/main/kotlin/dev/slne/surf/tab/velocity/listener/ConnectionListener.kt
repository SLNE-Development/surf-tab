package dev.slne.surf.tab.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.api.entry.TabGameMode
import dev.slne.surf.tab.velocity.hook.LuckPermsHook
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.service.tablistService
import dev.slne.surf.tab.velocity.tablistConfig
import dev.slne.surf.tab.velocity.util.formatWithAdventure
import dev.slne.surf.tab.velocity.util.getServers
import dev.slne.surf.tab.velocity.util.toTabProfile
import kotlin.jvm.optionals.getOrNull

class ConnectionListener {
    @Subscribe
    fun onPostConnect(event: ServerPostConnectEvent) {
        val player = event.player
        val server = player.currentServer.getOrNull() ?: return
        val seenServers = getSeenServers(server.server)
        val weight = LuckPermsHook.getWeight(player.uniqueId)
        tablistService.sendAdditions(
            player,
            tablistConfig.footer.formatWithAdventure(player),
            tablistConfig.header.formatWithAdventure(player)
        )

        seenServers.forEach { server ->
            server.playersConnected.forEach {
                tablistService.addPlayer(
                    it, TabEntry(
                        profile = it.gameProfile.toTabProfile(),
                        displayName = tablistConfig.nameFormat.formatWithAdventure(
                            player,
                            it
                        ),
                        ping = it.ping.toInt(),
                        gameMode = TabGameMode.SURVIVAL,
                        weight = weight
                    )
                )
            }
        }

        plugin.logger.info("Sent tablist additions for player ${event.player.username} (${event.player.uniqueId})")
    }

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        val player = event.player
        val server = player.currentServer.getOrNull() ?: return
        val seenServers = getSeenServers(server.server)

        seenServers.forEach { server ->
            server.playersConnected.forEach {
                tablistService.removePlayer(
                    it,
                    player.uniqueId
                )
            }
        }

        plugin.logger.info("Removed tablist entries for player ${event.player.username} (${event.player.uniqueId})")
    }

    private fun getSeenServers(base: RegisteredServer): List<RegisteredServer> {
        val groups = tablistConfig.groups.map { it.toTabGroup() }
        val relatedGroups = groups.filter { base in it.getServers() }

        return relatedGroups
            .flatMap { it.getServers() }
            .distinct()
    }
}