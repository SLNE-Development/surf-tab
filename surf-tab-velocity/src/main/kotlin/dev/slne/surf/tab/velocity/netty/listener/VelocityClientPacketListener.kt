package dev.slne.surf.tab.velocity.netty.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundSendTablistUpdatePacket
import org.springframework.stereotype.Component

@Component
class VelocityClientPacketListener {
    @SurfNettyPacketHandler
    fun handleTablistUpdatePacket(packet: ClientboundSendTablistUpdatePacket) {

    }
}