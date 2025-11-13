package dev.slne.surf.tab.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistAddPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistAdditionsPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistRemovePacket

class ConnectionListener {
    @Subscribe
    fun postConnect(event: ServerPostConnectEvent) {
        ServerboundTablistAdditionsPacket(event.player.uniqueId).fireAndForget()
        ServerboundTablistAddPacket(event.player.uniqueId).fireAndForget()
    }

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        ServerboundTablistRemovePacket(event.player.uniqueId).fireAndForget()
    }
}