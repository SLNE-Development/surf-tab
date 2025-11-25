package dev.slne.surf.tab.core.common.netty.packets.clientbound

import dev.slne.surf.cloud.api.common.meta.SurfNettyPacket
import dev.slne.surf.cloud.api.common.netty.network.protocol.PacketFlow
import dev.slne.surf.cloud.api.common.netty.packet.NettyPacket
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.tab.api.serializer.ComponentSerializer
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
@SurfNettyPacket("tablist:clientbound:additions", PacketFlow.CLIENTBOUND)
class ClientboundTablistAdditionsPacket(
    val player: CloudPlayer,
    val header: @Serializable(with = ComponentSerializer::class) Component,
    val footer: @Serializable(with = ComponentSerializer::class) Component
) : NettyPacket()