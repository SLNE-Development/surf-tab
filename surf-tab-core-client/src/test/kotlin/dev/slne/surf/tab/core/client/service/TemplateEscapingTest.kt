package dev.slne.surf.tab.core.client.service

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class TemplateEscapingTest {

    private val miniMessage: MiniMessage = MiniMessage.miniMessage()

    @Test
    fun `text that looks like a tag survives a serialise and deserialise round trip`() {
        val fragment = miniMessage.serialize(Component.text(" <> and <not_a_tag> "))
        val template = "<date>$fragment<time>"

        assertEquals(" <> and <not_a_tag> ", miniMessage.deserialize(fragment).plain())

        val analyzed = TablistTemplate.analyze(template, miniMessage)
        assertEquals(setOf(BuiltinPlaceholder.DATE, BuiltinPlaceholder.TIME), analyzed.placeholders)
        assertEquals(emptySet<String>(), analyzed.unknownTags, "escaped text is not a placeholder")
        assertFalse(analyzed.rendersPerPlayer)
    }
}
