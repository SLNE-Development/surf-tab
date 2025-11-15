package dev.slne.surf.tab.core.common.netty.packets.serverbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@SurfNettyPacket("tablist:serverbound:remove", PacketFlow.SERVERBOUND)
class ServerboundTablistRemovePacket(
    val uuid: @Contextual UUID,
    val senderServer: String
) : NettyPacket()