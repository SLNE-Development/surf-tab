package dev.slne.surf.tab.core.common.netty.packets.serverbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Serializable

@Serializable
@SurfNettyPacket("tablist:serverbound:additions", PacketFlow.SERVERBOUND)
class ServerboundTablistAdditionsPacket() : NettyPacket()