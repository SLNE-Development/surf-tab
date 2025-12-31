package dev.slne.surf.tab.velocity.listener

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.KickedFromServerEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import com.velocitypowered.api.proxy.Player
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.redis.event.TabEntryAddRedisEvent
import dev.slne.surf.tab.velocity.redisApi
import dev.slne.surf.tab.velocity.service.tablistService
import java.util.concurrent.TimeUnit

class ConnectionListener {
    @Subscribe
    fun onPostConnect(event: ServerPostConnectEvent) {
        plugin.proxy.scheduler.buildTask(plugin, Runnable {
            handleJoin(event.player)
        }).delay(750, TimeUnit.MILLISECONDS).schedule()
    }

    private fun handleJoin(player: Player) {
        redisApi.publishEvent(
            TabEntryAddRedisEvent(
                player.uniqueId
            )
        )

        tablistService.sendAdditions(player)

        plugin.pluginContainer.launch {
            tablistService.formatOnlinePlayers(player)
        }
    }

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        val player = event.player


    }

    @Subscribe
    fun onKick(event: KickedFromServerEvent) {
        val player = event.player
    }
}
