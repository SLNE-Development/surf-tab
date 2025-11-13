package dev.slne.surf.tab.velocity.netty.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistAddPacket
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistAdditionsPacket
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistRemovePacket
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.service.VelocityTablistService

class VelocityPacketListener(
    private val tablistService: VelocityTablistService
) {
    @SurfNettyPacketHandler
    fun handleAddPacket(packet: ClientboundTablistAddPacket) {
        plugin.proxy.allPlayers.mapNotNull { CloudPlayer[it.uniqueId] }.forEach {
            tablistService.addPlayer(it, packet.entry)
        }
    }

    @SurfNettyPacketHandler
    fun handleRemovePacket(packet: ClientboundTablistRemovePacket) {
        plugin.proxy.allPlayers.mapNotNull { CloudPlayer[it.uniqueId] }.forEach {
            tablistService.removePlayer(it, packet.profileName)
        }
    }

    @SurfNettyPacketHandler
    fun handleAdditionsPacket(packet: ClientboundTablistAdditionsPacket) {
        val player = packet.player

        if (player != null) {
            tablistService.sendAdditions(player, packet.header, packet.footer)
            return
        }

        plugin.proxy.allPlayers.mapNotNull { CloudPlayer[it.uniqueId] }.forEach {
            tablistService.sendAdditions(it, packet.header, packet.footer)
        }
    }
}
