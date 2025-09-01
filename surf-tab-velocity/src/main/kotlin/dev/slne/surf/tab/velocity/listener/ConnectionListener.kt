package dev.slne.surf.tab.velocity.listener

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import dev.slne.surf.tab.core.service.tabService
import dev.slne.surf.tab.velocity.container
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.util.tabPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class ConnectionListener {
    @Subscribe
    fun onConnect(event: ServerPostConnectEvent) {
        container.launch(Dispatchers.IO) {
            delay(1000L)

            plugin.proxy.allPlayers.forEach {
                tabService.sendTablistUpdate(it.tabPlayer())
            }
        }
    }
}