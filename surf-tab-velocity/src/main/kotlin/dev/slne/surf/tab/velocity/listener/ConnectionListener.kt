package dev.slne.surf.tab.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.proxy.Player
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.service.tablistService
import java.util.concurrent.TimeUnit
import kotlin.jvm.optionals.getOrNull

class ConnectionListener {
    @Subscribe
    fun onPostConnect(event: ServerPostConnectEvent) {
        plugin.proxy.scheduler.buildTask(plugin, Runnable {
            handleJoin(event.player)
        }).delay(750, TimeUnit.MILLISECONDS).schedule()
    }

    private fun handleJoin(player: Player) {
        val server = player.currentServer.getOrNull()?.server ?: return

        val seenServers = tablistService.getSeenServers(server)
        val visiblePlayers = seenServers.flatMap { it.playersConnected }

        tablistService.sendAdditions(player)

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
            .forEach { other ->
                tablistService.removePlayer(other, player.uniqueId)
            }
    }
}
