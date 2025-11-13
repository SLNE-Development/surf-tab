package dev.slne.surf.tab.core.common.netty.packets.serverbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import dev.slne.surf.cloud.api.common.server.CommonCloudServer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@SurfNettyPacket("tablist:serverbound:remove", PacketFlow.SERVERBOUND)
class ServerboundTablistRemovePacket(
    val player: @Contextual UUID,
    val senderServer: CommonCloudServer
) : NettyPacket()