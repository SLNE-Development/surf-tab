package dev.slne.surf.tab.server.netty.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.server.netty.packet.broadcast
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.api.entry.TabGameMode
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistAddPacket
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistAdditionsPacket
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistRemovePacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundReloadPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistAddPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistAdditionsPacket
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundTablistRemovePacket
import dev.slne.surf.tab.server.config
import dev.slne.surf.tab.server.placeholder.PlaceholderManager
import dev.slne.surf.tab.server.plugin
import org.springframework.stereotype.Component

@Component
class ServerPacketListener {
    @SurfNettyPacketHandler
    suspend fun handleAddPacket(packet: ServerboundTablistAddPacket) {
        val profile = packet.profile
        val cloudPlayer = CloudPlayer[profile.uuid] ?: return
        val luckpermsMetaWeight = cloudPlayer.getLuckpermsMetaData("weight")?.toIntOrNull() ?: 0

        val displayName = PlaceholderManager.parseAsync(config.nameFormat, cloudPlayer)

        ClientboundTablistAddPacket(
            TabEntry(
                profile = profile,
                displayName = displayName,
                gameMode = TabGameMode.SURVIVAL,
                ping = 0,
                weight = luckpermsMetaWeight
            )
        ).broadcast {
            getSeenServers(packet.senderServer).contains(it.hostname)
        }
    }

    @SurfNettyPacketHandler
    suspend fun handleRemovePacket(packet: ServerboundTablistRemovePacket) =
        ClientboundTablistRemovePacket(
            packet.uuid
        ).broadcast {
            getSeenServers(packet.senderServer).contains(it.hostname)
        }

    @SurfNettyPacketHandler
    suspend fun handleAdditionsPacket(packet: ServerboundTablistAdditionsPacket) {
        val player = CloudPlayer[packet.player] ?: return

        ClientboundTablistAdditionsPacket(
            player = player.uuid,
            header = PlaceholderManager.parseAsync(config.header, player),
            footer = PlaceholderManager.parseAsync(config.footer, player)
        ).broadcast()
    }

    @SurfNettyPacketHandler
    fun handleReloadPacket(packet: ServerboundReloadPacket) {
        plugin.configuration.reload()

        CloudPlayer.all().forEach {
            ClientboundTablistAdditionsPacket(
                player = it.uuid,
                header = PlaceholderManager.parse(config.header, it),
                footer = PlaceholderManager.parse(config.footer, it)
            ).broadcast()
        }
    }

    private fun getSeenServers(base: String): List<String> {
        val groups = config.groups.map { it.toTabGroup() }
        val relatedGroups = groups.filter { base in it.clients }

        return relatedGroups
            .flatMap { it.clients }
            .distinct()
    }
}