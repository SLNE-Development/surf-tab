package dev.slne.surf.tab.velocity.netty.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistAdditionsPacket
import dev.slne.surf.tab.velocity.service.VelocityTablistService
import org.springframework.stereotype.Component

@Component
class VelocityPacketListener(
    private val tablistService: VelocityTablistService
) {
    @SurfNettyPacketHandler
    fun handleAdditionsPacket(packet: ClientboundTablistAdditionsPacket) {
        val player = CloudPlayer[packet.player] ?: return

        tablistService.sendAdditions(player, packet.header, packet.footer)
    }
}
