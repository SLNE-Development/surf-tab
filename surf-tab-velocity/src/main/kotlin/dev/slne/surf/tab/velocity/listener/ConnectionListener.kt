package dev.slne.surf.tab.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.proxy.Player
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.redis.event.TabEntryAddRedisEvent
import dev.slne.surf.tab.velocity.redis.event.TabEntryRemoveRedisEvent
import dev.slne.surf.tab.velocity.redisApi
import dev.slne.surf.tab.velocity.service.tablistService
import java.util.concurrent.TimeUnit
import kotlin.jvm.optionals.getOrNull

class ConnectionListener {
    @Subscribe
    fun onPostConnect(event: ServerPostConnectEvent) {
        event.previousServer?.let {
            redisApi.publishEvent(TabEntryRemoveRedisEvent(event.player.uniqueId, it))
        }

        plugin.proxy.scheduler.buildTask(plugin, Runnable {
            handleJoin(event.player)
        }).delay(750, TimeUnit.MILLISECONDS).schedule()
    }

    private fun handleJoin(player: Player) {
        val server = player.currentServer.getOrNull()?.server ?: return

        redisApi.publishEvent(
            TabEntryAddRedisEvent(
                tablistService.createEntry(player), server
            )
        )

        tablistService.sendAdditions(player)
        tablistService.sendCurrentTablist(player)
    }

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        val player = event.player
        val server = player.currentServer.getOrNull()?.server ?: return

        redisApi.publishEvent(
            TabEntryRemoveRedisEvent(
                player.uniqueId, server
            )
        )
    }

    @Subscribe
    fun onKick(event: KickedFromServerEvent) {
        val player = event.player
        val server = event.server

        redisApi.publishEvent(
            TabEntryRemoveRedisEvent(
                player.uniqueId, server
            )
        )
    }
}
