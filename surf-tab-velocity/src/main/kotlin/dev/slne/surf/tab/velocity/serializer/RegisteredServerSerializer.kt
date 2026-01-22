package dev.slne.surf.tab.velocity.serializer

import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.slne.surf.tab.velocity.plugin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias SerializableRegisteredServer = @Serializable(with = RegisteredServerSerializer::class) RegisteredServer

object RegisteredServerSerializer : KSerializer<RegisteredServer> {
    override val descriptor = PrimitiveSerialDescriptor("RegisteredServer", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: RegisteredServer
    ) {
        encoder.encodeString(value.serverInfo.name)
    }

    override fun deserialize(decoder: Decoder) =
        plugin.proxy.getServer(decoder.decodeString()).get()
}