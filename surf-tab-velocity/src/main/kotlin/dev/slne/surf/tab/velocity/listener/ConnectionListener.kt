package dev.slne.surf.tab.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import dev.slne.surf.tab.velocity.service.tablistService
import dev.slne.surf.tab.velocity.tablistConfig
import dev.slne.surf.tab.velocity.util.formatWithAdventure
import kotlin.jvm.optionals.getOrNull

class ConnectionListener {
    @Subscribe
    fun onPostConnect(event: ServerPostConnectEvent) {
        val player = event.player
        val server = player.currentServer.getOrNull()?.server ?: return

        val seenServers = tablistService.getSeenServers(server)
        val visiblePlayers = seenServers.flatMap { it.playersConnected }.distinct()

        tablistService.sendAdditions(
            player,
            tablistConfig.header.formatWithAdventure(player),
            tablistConfig.footer.formatWithAdventure(player)
        )

        visiblePlayers.forEach { other ->
            tablistService.addPlayer(
                player,
                tablistService.createEntry(other, player)
            )
        }

        visiblePlayers.forEach { other ->
            tablistService.addPlayer(
                other,
                tablistService.createEntry(player, other)
            )
        }
    }

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        val player = event.player
        val server = player.currentServer.getOrNull()?.server ?: return

        tablistService.getSeenServers(server)
            .flatMap { it.playersConnected }
            .distinct()
            .forEach { other ->
                tablistService.removePlayer(other, player.uniqueId)
            }
    }
}
