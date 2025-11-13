package dev.slne.surf.tab.server.netty.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.server.netty.packet.broadcast
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.api.entry.TabGameMode
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistAddPacket
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistAdditionsPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistAddPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistAdditionsPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistRemovePacket
import dev.slne.surf.tab.server.config
import net.kyori.adventure.text.format.NamedTextColor
import org.springframework.stereotype.Component

@Component
class ServerPacketListener {
    @SurfNettyPacketHandler
    suspend fun handleAddPacket(packet: ServerboundTablistAddPacket) {
        val profile = packet.profile
        val cloudPlayer = CloudPlayer[profile.uuid] ?: return
        val luckpermsMetaWeight = cloudPlayer.getLuckpermsMetaData("weight")?.toIntOrNull() ?: 0

        ClientboundTablistAddPacket(
            TabEntry(
                profile = profile,
                displayName = text(profile.name, NamedTextColor.WHITE),
                gameMode = TabGameMode.SURVIVAL,
                ping = 0,
                weight = luckpermsMetaWeight
            )
        ).broadcast {
            getSeenServers(packet.senderServer.name).contains(it.hostname)
        }
    }

    @SurfNettyPacketHandler
    suspend fun handleRemovePacket(packet: ServerboundTablistRemovePacket) {
        val profile = packet.profile
        val cloudPlayer = CloudPlayer[profile.uuid] ?: return
        val luckpermsMetaWeight = cloudPlayer.getLuckpermsMetaData("weight")?.toIntOrNull() ?: 0

        ClientboundTablistAddPacket(
            TabEntry(
                profile = profile,
                displayName = text(profile.name, NamedTextColor.WHITE),
                gameMode = TabGameMode.SURVIVAL,
                ping = 0,
                weight = luckpermsMetaWeight
            )
        ).broadcast {
            getSeenServers(packet.senderServer.name).contains(it.hostname)
        }
    }

    @SurfNettyPacketHandler
    fun handleAdditionsPacket(packet: ServerboundTablistAdditionsPacket) {
        ClientboundTablistAdditionsPacket(
            player = packet.player?.let { CloudPlayer[it] },
            header = config.header,
            footer = config.footer
        ).broadcast()
    }

    private fun getSeenServers(base: String): List<String> {
        val groups = config.groups.map { it.toTabGroup() }
        val relatedGroups = groups.filter { base in it.clients }

        return relatedGroups
            .flatMap { it.clients }
            .distinct()
    }
}