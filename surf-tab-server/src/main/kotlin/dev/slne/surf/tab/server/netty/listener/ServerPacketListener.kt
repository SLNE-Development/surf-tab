package dev.slne.surf.tab.server.netty.listener

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacketHandler
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.server.netty.packet.broadcast
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.api.entry.TabGameMode
import dev.slne.surf.tab.core.common.SyncValues
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistAdditionsPacket
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

        logger().atInfo()
            .log("Groups: ${config.groups}, seen servers: ${getSeenServers(packet.senderServer)}")

        getSeenServers(packet.senderServer).forEach {
            logger().atInfo()
                .log("Adding tablist entry for player ${profile.name} (${profile.uuid}) on server $it")
            SyncValues.tabEntries.add(
                it to TabEntry(
                    profile = profile,
                    displayName = displayName,
                    gameMode = TabGameMode.SURVIVAL,
                    ping = 0,
                    weight = luckpermsMetaWeight
                )
            )
        }
    }

    @SurfNettyPacketHandler
    suspend fun handleRemovePacket(packet: ServerboundTablistRemovePacket) =
        getSeenServers(packet.senderServer).forEach { _ ->
            logger().atInfo().log("Removing tablist entry for player with UUID ${packet.uuid}")
            SyncValues.tabEntries.removeIf { it.second.profile.uuid == packet.uuid }
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

        val oldEntries = SyncValues.tabEntries.snapshot()

        SyncValues.tabEntries.clear()
        oldEntries.forEach { (server, entry) ->
            SyncValues.tabEntries.add(server to entry)
        }

        logger().atInfo()
            .log("Successfully reloaded tablist configuration & updated all players' tablists.")
    }

    private fun getSeenServers(base: String): List<String> {
        val groups = config.groups.map { it.toTabGroup() }
        val relatedGroups = groups.filter { base in it.clients }

        return relatedGroups
            .flatMap { it.clients }
            .distinct()
    }
}