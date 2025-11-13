package dev.slne.surf.tab.core.common.netty.packets.serverbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import dev.slne.surf.cloud.api.common.server.CommonCloudServer
import dev.slne.surf.tab.api.entry.TabProfile
import kotlinx.serialization.Serializable

@Serializable
@SurfNettyPacket("tablist:serverbound:remove", PacketFlow.SERVERBOUND)
class ServerboundTablistRemovePacket(
    val profile: TabProfile,
    val senderServer: CommonCloudServer
) : NettyPacket()