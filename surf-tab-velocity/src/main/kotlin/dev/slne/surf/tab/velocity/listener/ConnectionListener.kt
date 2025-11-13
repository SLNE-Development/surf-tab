package dev.slne.surf.tab.velocity.listener

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerPostConnectEvent
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.server.CommonCloudServer
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistAddPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistAdditionsPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistRemovePacket
import dev.slne.surf.tab.velocity.util.toTabProfile

class ConnectionListener {
    @Subscribe
    fun postConnect(event: ServerPostConnectEvent) {
        ServerboundTablistAdditionsPacket(event.player.uniqueId).fireAndForget()
        ServerboundTablistAddPacket(
            event.player.gameProfile.toTabProfile(),
            CommonCloudServer.current()
        ).fireAndForget()
    }

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        ServerboundTablistRemovePacket(
            event.player.gameProfile.toTabProfile(),
            CommonCloudServer.current()
        ).fireAndForget()
    }
}