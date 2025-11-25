package dev.slne.surf.tab.api.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer

class ComponentSerializer : KSerializer<Component> {
    override val descriptor = PrimitiveSerialDescriptor("Component", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Component
    ) = encoder.encodeString(GsonComponentSerializer.gson().serialize(value))

    override fun deserialize(decoder: Decoder) =
        GsonComponentSerializer.gson().deserialize(decoder.decodeString())
}