package dev.slne.surf.tab.server.netty.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.cloud.api.server.netty.packet.broadcast
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistAdditionsPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistAddPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistAdditionsPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistRemovePacket
import dev.slne.surf.tab.server.config
import org.springframework.stereotype.Component

@Component
class ServerPacketListener {
    @SurfNettyPacketHandler
    fun handleAddPacket(packet: ServerboundTablistAddPacket) {

    }

    @SurfNettyPacketHandler
    fun handleRemovePacket(packet: ServerboundTablistRemovePacket) {

    }

    @SurfNettyPacketHandler
    fun handleAdditionsPacket(packet: ServerboundTablistAdditionsPacket) {
        ClientboundTablistAdditionsPacket(
            header = config.header,
            footer = config.footer,
            nameFormat = config.nameFormat
        ).broadcast()
    }
}