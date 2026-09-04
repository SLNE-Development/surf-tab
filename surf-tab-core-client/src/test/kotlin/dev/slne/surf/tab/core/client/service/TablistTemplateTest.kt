package dev.slne.surf.tab.core.client.service

import dev.slne.surf.tab.api.placeholder.TabPlaceholder
import dev.slne.surf.tab.core.client.config.TablistConfig
import dev.slne.surf.tab.core.client.service.TablistUpdateReason
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TablistTemplateTest {

    private val miniMessage: MiniMessage = MiniMessage.miniMessage()

    private fun analyze(source: String) = TablistTemplate.analyze(source, miniMessage)

    private fun values(
        generation: Long = 1,
        server: String = "lobby-1",
        onlinePlayers: Int = 5,
        maxPlayers: Int = 1000,
        date: String = "07.03.2024",
        time: String = "09:05"
    ) = TablistValues(
        generation,
        mapOf(
            BuiltinPlaceholder.SERVER to Component.text(server),
            BuiltinPlaceholder.PLAYERS_ONLINE to Component.text(onlinePlayers),
            BuiltinPlaceholder.PLAYERS_MAX to Component.text(maxPlayers),
            BuiltinPlaceholder.DATE to Component.text(date),
            BuiltinPlaceholder.TIME to Component.text(time)
        ),
        onlinePlayers
    )

    @Test
    fun `a template knows which placeholders it names`() {
        val template = analyze("<date> <gray>-</gray> <time> <players_online>")

        assertTrue(template.dependsOn(BuiltinPlaceholder.DATE))
        assertTrue(template.dependsOn(BuiltinPlaceholder.TIME))
        assertTrue(template.dependsOn(BuiltinPlaceholder.PLAYERS_ONLINE))
        assertFalse(template.dependsOn(BuiltinPlaceholder.PLAYERS_MAX))
        assertFalse(template.dependsOn(BuiltinPlaceholder.SERVER))
    }

    @Test
    fun `colours and line breaks are not something a template depends on`() {
        val template = analyze("<br><#6EA6D9>CASTCRAFTER<br><bold><gradient:red:blue>hi</gradient>")

        assertEquals(emptySet<TabPlaceholder>(), template.placeholders)
        assertEquals(emptySet<String>(), template.unknownTags)
        assertFalse(template.rendersPerPlayer)
    }

    @Test
    fun `a tag nobody accounts for makes a template render per player`() {
        val template = analyze("<gray>ping: <player_ping>")

        assertEquals(setOf("player_ping"), template.unknownTags)
        assertTrue(template.rendersPerPlayer)
    }

    @Test
    fun `a tag hidden inside the argument of another tag is still found`() {
        // MiniMessage does the reading, so a placeholder that a plain search over the text would walk
        // straight past is still recognised as something the template depends on.
        val template = analyze("<hover:show_text:'<time> - <player_ping>'>hover me</hover>")

        assertTrue(template.dependsOn(BuiltinPlaceholder.TIME))
        assertEquals(setOf("player_ping"), template.unknownTags)
    }

    @Test
    fun `an escaped placeholder is not something a template depends on`() {
        val template = analyze("\\<time> is written out rather than filled in")

        assertEquals(emptySet<TabPlaceholder>(), template.placeholders)
        assertFalse(template.rendersPerPlayer)
    }

    @Test
    fun `the shipped header depends on the date, the time and the number of players`() {
        val template = analyze(TablistConfig().header)

        assertEquals(
            setOf(
                BuiltinPlaceholder.DATE,
                BuiltinPlaceholder.TIME,
                BuiltinPlaceholder.PLAYERS_ONLINE,
                BuiltinPlaceholder.PLAYERS_MAX
            ),
            template.placeholders
        )
        assertEquals(emptySet<String>(), template.unknownTags)
        assertFalse(template.rendersPerPlayer)
    }

    @Test
    fun `the shipped footer depends on nothing that ever changes`() {
        val template = analyze(TablistConfig().footer)

        assertEquals(setOf(BuiltinPlaceholder.SERVER), template.placeholders)
        assertFalse(template.rendersPerPlayer)
        assertFalse(
            template.inputsChanged(values(), values(generation = 2, onlinePlayers = 900, time = "23:59")),
            "a footer naming only the server cannot be changed by the clock or by who is online"
        )
    }

    @Test
    fun `a render is reused while the values behind it stand still`() {
        val template = analyze("<time>")
        var renders = 0

        val first = template.renderShared(values()) { renders++; Component.text("09:05") }
        val second = template.renderShared(values(generation = 2)) { renders++; Component.text("09:05") }

        assertEquals(1, renders, "nothing the template names moved, so nothing had to be rendered")
        assertSame(first, second, "everybody is handed the very same component")
    }

    @Test
    fun `a render is redone once a value the template names moves`() {
        val template = analyze("<time>")
        var renders = 0

        template.renderShared(values()) { renders++; Component.text("09:05") }
        template.renderShared(values(generation = 2, time = "09:06")) { renders++; Component.text("09:06") }

        assertEquals(2, renders)
    }

    @Test
    fun `a value the template does not name does not cost a render`() {
        val template = analyze("<time>")
        var renders = 0

        template.renderShared(values()) { renders++; Component.text("09:05") }
        template.renderShared(values(generation = 2, onlinePlayers = 900)) {
            renders++
            Component.text("09:05")
        }

        assertEquals(1, renders)
    }

    @Test
    fun `the header and the footer are asked about separately`() {
        val templates = TablistTemplates.analyze("<time>", "<players_online>", miniMessage)

        assertTrue(templates.header.dependsOn(BuiltinPlaceholder.TIME))
        assertFalse(templates.footer.dependsOn(BuiltinPlaceholder.TIME))
        assertFalse(templates.header.dependsOn(BuiltinPlaceholder.PLAYERS_ONLINE))
        assertTrue(templates.footer.dependsOn(BuiltinPlaceholder.PLAYERS_ONLINE))
    }

    @Test
    fun `an update only happens for a reason a template can be affected by`() {
        val clockOnly = TablistTemplates.analyze("<time>", "<red>hello", miniMessage)

        assertTrue(clockOnly.affectedBy(TablistUpdateReason.CLOCK))
        assertFalse(clockOnly.affectedBy(TablistUpdateReason.PLAYER_COUNT))
        assertFalse(clockOnly.affectedBy(TablistUpdateReason.UnknownPlaceholders))
        assertTrue(clockOnly.affectedBy(TablistUpdateReason.Configuration))
    }

    @Test
    fun `a template that never changes on its own is affected by nothing but a reload`() {
        val static = TablistTemplates.analyze("<red>hello", "<blue>bye", miniMessage)

        assertFalse(static.affectedBy(TablistUpdateReason.CLOCK))
        assertFalse(static.affectedBy(TablistUpdateReason.PLAYER_COUNT))
        assertFalse(static.affectedBy(TablistUpdateReason.UnknownPlaceholders))
        assertTrue(static.affectedBy(TablistUpdateReason.Configuration))
        assertFalse(static.usesClock)
        assertFalse(static.rendersPerPlayer)
    }

    @Test
    fun `an unknown placeholder in either template asks for the fallback`() {
        val templates = TablistTemplates.analyze("<red>hello", "<player_ping>", miniMessage)

        assertTrue(templates.affectedBy(TablistUpdateReason.UnknownPlaceholders))
        assertTrue(templates.rendersPerPlayer)
        assertFalse(templates.header.rendersPerPlayer, "only the footer named something unknown")
        assertTrue(templates.footer.rendersPerPlayer)
    }

    @Test
    fun `analysed templates recognise the configuration they came from`() {
        val templates = TablistTemplates.analyze("<time>", "<server>", miniMessage)

        assertTrue(templates.matches("<time>", "<server>"))
        assertFalse(templates.matches("<time>", "<date>"))
    }
}
