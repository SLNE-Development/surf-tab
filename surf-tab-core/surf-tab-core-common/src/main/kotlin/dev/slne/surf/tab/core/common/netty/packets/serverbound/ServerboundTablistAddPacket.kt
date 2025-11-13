package dev.slne.surf.tab.core.common.netty.packets.serverbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import kotlinx.serialization.Serializable

@Serializable
@SurfNettyPacket("tablist:serverbound:add", PacketFlow.SERVERBOUND)
class ServerboundTablistAddPacket(val player: CloudPlayer) : NettyPacket()