package dev.slne.surf.tab.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import dev.slne.surf.tab.velocity.service.VelocityTablistService

class ConnectionListener(
    private val tablistService: VelocityTablistService
) {
    @Subscribe
    fun postConnect(event: ServerPostConnectEvent) {
        tablistService.sendTablistUpdate()
    }
}