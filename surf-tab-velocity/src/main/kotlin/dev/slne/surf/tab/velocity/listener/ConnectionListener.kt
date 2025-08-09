package dev.slne.surf.tab.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import dev.slne.surf.tab.core.service.tabService
import dev.slne.surf.tab.velocity.util.tabPlayer

class ConnectionListener {
    @Subscribe
    fun onConnect(event: ServerPostConnectEvent) {
        val player = event.player
        val tabPlayer = player.tabPlayer()

        tabService.sendTablistUpdate(tabPlayer)
    }
}