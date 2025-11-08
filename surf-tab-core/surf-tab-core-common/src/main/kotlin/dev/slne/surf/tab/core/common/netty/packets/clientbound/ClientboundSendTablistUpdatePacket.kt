package dev.slne.surf.tab.core.common.netty.packets.clientbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import dev.slne.surf.tab.api.entry.TabEntry
import kotlinx.serialization.Serializable

@Serializable
@SurfNettyPacket("tablist:clientbound:update", PacketFlow.CLIENTBOUND)
class ClientboundSendTablistUpdatePacket(val entries: List<TabEntry>) : NettyPacket()