package dev.slne.surf.tab.core.common.netty.packets.clientbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import kotlinx.serialization.Serializable

@Serializable
@SurfNettyPacket("tablist:clientbound:remove", PacketFlow.CLIENTBOUND)
class ClientboundTablistRemovePacket(val profileName: String) : NettyPacket()