package dev.slne.surf.tab.core.client.service

import dev.slne.surf.tab.core.client.platform.TabPlayer
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag.selfClosingInserting
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.resolver
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A tablist standing on its own, so that what it renders and what it sends can be observed without a
 * server underneath it.
 *
 * Everything the real service reads out of the platform is a plain field here, so a test moves the
 * clock on or lets somebody join by assigning to it.
 */
internal class TestTablist(header: String, footer: String) {

    val miniMessage: MiniMessage = MiniMessage.miniMessage()

    var server = "lobby-1"
    var maxPlayers = 1000
    var date = "07.03.2024"
    var time = "09:05"

    val players = CopyOnWriteArrayList<TestPlayer>()

    var templates = TablistTemplates.analyze(header, footer, miniMessage)

    /** Runs once per update, before anything is read. */
    var beforeUpdate: () -> Unit = {}

    /** Runs once per update of everybody, after the players to update were chosen. */
    var duringUpdate: () -> Unit = {}

    /** Set to pretend an update is built from an older or newer snapshot than it really is. */
    var generationOverride: Long? = null

    /** Where updates of everybody run. Inline by default, so that tests read top to bottom. */
    var updateRunner: (suspend () -> Unit) -> Unit = { block -> runBlocking { block() } }

    private val generations = java.util.concurrent.atomic.AtomicLong()

    val renderer = TestRenderer()

    val additions = TablistAdditions(
        templates = { templates },
        captureValues = {
            beforeUpdate()
            TablistValues(
                generation = generationOverride ?: generations.incrementAndGet(),
                server = server,
                onlinePlayers = players.size,
                maxPlayers = maxPlayers,
                date = date,
                time = time
            )
        },
        onlinePlayers = { players.toList().also { duringUpdate() } },
        onlinePlayerCount = { players.size },
        renderer = renderer,
        runUpdates = { block -> updateRunner(block) }
    )

    /**
     * Lets [player] join, the way the platform listeners do.
     */
    fun join(player: TestPlayer): TestPlayer {
        players += player
        additions.invalidatePlayer(player)
        additions.invalidateAll(TablistUpdateReason.PLAYER_COUNT)

        return player
    }

    /**
     * Lets [player] leave, the way the platform listeners do.
     */
    fun leave(player: TestPlayer) {
        players -= player
        additions.forget(player.uuid)
        additions.invalidateAll(TablistUpdateReason.PLAYER_COUNT)
    }

    /**
     * Renders the templates the way [TablistAdditions] does, so that a test can say what it expects
     * a player to be shown without spelling out MiniMessage.
     */
    inner class TestRenderer : TablistRenderer {

        /** Every template that was rendered, in order, so that renders can be counted per template. */
        val rendered = CopyOnWriteArrayList<String>()

        override fun placeholders(values: TablistValues): TagResolver = resolver(
            tag(TablistPlaceholder.SERVER, values.server),
            tag(TablistPlaceholder.PLAYERS_ONLINE, values.onlinePlayers.toString()),
            tag(TablistPlaceholder.PLAYERS_MAX, values.maxPlayers.toString()),
            tag(TablistPlaceholder.DATE, values.date),
            tag(TablistPlaceholder.TIME, values.time)
        )

        override fun render(
            template: String,
            player: TabPlayer?,
            placeholders: TagResolver
        ): Component {
            rendered += template

            // Stands in for the MiniPlaceholders resolvers, which are the ones that can render
            // differently depending on who is being rendered for.
            val audience = if (player is TestPlayer) {
                resolver("player_name", selfClosingInserting(Component.text(player.name)))
            } else {
                TagResolver.empty()
            }

            return miniMessage.deserialize(template, resolver(placeholders, audience))
        }

        /** How often [template] was rendered. */
        fun rendersOf(template: String) = rendered.count { it == template }

        private fun tag(placeholder: TablistPlaceholder, value: String) =
            resolver(placeholder.tagName, selfClosingInserting(Component.text(value)))
    }
}

/**
 * A player who writes down every header and footer they are sent.
 */
internal class TestPlayer(val name: String, override val uuid: UUID = UUID.randomUUID()) : TabPlayer {

    val received = CopyOnWriteArrayList<Pair<Component, Component>>()

    override val audience = recordingAudience { header, footer -> received += header to footer }

    override fun baseName(): Component = Component.text(name)

    override suspend fun baseNameSnapshot(): Component = baseName()

    override suspend fun showTabEntry(name: Component, order: Int) = Unit

    /** The header this player is currently showing, as plain text. */
    fun header() = received.last().first.plain()

    /** The footer this player is currently showing, as plain text. */
    fun footer() = received.last().second.plain()
}

/**
 * An audience that only reacts to a header and a footer being sent to it.
 */
private fun recordingAudience(send: (Component, Component) -> Unit) = object : Audience {
    override fun sendPlayerListHeaderAndFooter(header: Component, footer: Component) =
        send(header, footer)
}

internal fun Component.plain(): String = PlainTextComponentSerializer.plainText().serialize(this)
