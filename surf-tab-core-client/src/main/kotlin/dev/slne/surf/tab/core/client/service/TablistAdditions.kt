package dev.slne.surf.tab.core.client.service

import dev.slne.surf.tab.core.client.platform.TabViewer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Renders the header and the footer and sends them only when they actually changed.
 *
 * @param templates the currently configured templates
 * @param captureValues takes a snapshot of everything the tablist fills its own placeholders with
 * @param onlinePlayers everybody who should be shown a header and a footer
 * @param onlinePlayerCount how many players are online, without building the collection
 * @param renderer turns a template into a component
 * @param runUpdates schedules an update on the scope updates must run on
 */
class TablistAdditions(
    private val templates: () -> TablistTemplates,
    private val captureValues: () -> TablistValues,
    private val onlinePlayers: () -> Collection<TabViewer>,
    private val onlinePlayerCount: () -> Int,
    private val renderer: TablistRenderer,
    runUpdates: (suspend () -> Unit) -> Unit
) {

    /**
     * What every player was last sent, so that an unchanged header and footer can be left alone.
     */
    private val sent = ConcurrentHashMap<UUID, SentAdditions>()

    private val updates = UpdateCoalescer<TablistUpdateReason>(runUpdates) { reason ->
        updateEveryone(reason)
    }

    /**
     * Asks for everybody's header and footer to be brought up to date because of [reason].
     */
    fun invalidateAll(reason: TablistUpdateReason) {
        if (!templates().affectedBy(reason)) return

        updates.request(EVERYONE, reason)
    }

    /**
     * Brings [player]'s header and footer up to date right away.
     */
    fun invalidatePlayer(player: TabViewer) {
        val templates = templates()
        val values = captureValues()
        val placeholders = renderer.placeholders(values)

        send(
            player,
            values,
            render(templates.header, player, values, placeholders),
            render(templates.footer, player, values, placeholders)
        )
    }

    /**
     * Forgets what [playerUuid] was sent, because they left.
     */
    fun forget(playerUuid: UUID) {
        sent.remove(playerUuid)
    }

    /**
     * Brings everybody's header and footer up to date.
     */
    private fun updateEveryone(reason: TablistUpdateReason) {
        val templates = templates()
        val values = captureValues()
        val placeholders = renderer.placeholders(values)

        val sharedHeader = shared(templates.header, values, placeholders)
        val sharedFooter = shared(templates.footer, values, placeholders)

        for (player in onlinePlayers()) {
            send(
                player,
                values,
                sharedHeader ?: renderer.render(templates.header.source, player, placeholders),
                sharedFooter ?: renderer.render(templates.footer.source, player, placeholders)
            )
        }

        forgetPlayersNotVisitedBy(values)

        if (onlinePlayerCount() != values.onlinePlayers) {
            invalidateAll(TablistUpdateReason.PLAYER_COUNT)
        }
    }

    /**
     * The component of [template] that every player can be shown, or `null` when it has to be
     * rendered per player.
     */
    private fun shared(
        template: TablistTemplate,
        values: TablistValues,
        placeholders: TagResolver
    ) = if (template.rendersPerPlayer) {
        null
    } else {
        template.renderShared(values) { renderer.render(template.source, null, placeholders) }
    }

    /**
     * Renders [template] for [player], sharing the render with everybody else where that is allowed.
     */
    private fun render(
        template: TablistTemplate,
        player: TabViewer,
        values: TablistValues,
        placeholders: TagResolver
    ) = shared(template, values, placeholders)
        ?: renderer.render(template.source, player, placeholders)

    /**
     * Sends [header] and [footer] to [player], unless they are already showing them.
     */
    private fun send(
        player: TabViewer,
        values: TablistValues,
        header: Component,
        footer: Component
    ) {
        sent.computeIfAbsent(player.uuid) { SentAdditions() }
            .send(player, values, header, footer)
    }

    /**
     * Drops everybody the update behind [values] did not visit.
     */
    private fun forgetPlayersNotVisitedBy(values: TablistValues) {
        sent.values.removeIf { it.olderThan(values.generation) }
    }

    private companion object {

        /**
         * The key the update of everybody is collapsed under.
         */
        private val EVERYONE: UUID = UUID(0, 0)
    }
}

/**
 * What one player was last sent.
 */
private class SentAdditions {

    /**
     * The newest [TablistValues.generation] this player was brought up to date with.
     */
    @Volatile
    private var generation = Long.MIN_VALUE

    private var header: Component? = null
    private var footer: Component? = null

    /**
     * Whether this player was last updated by something older than [generation].
     */
    fun olderThan(generation: Long) = this.generation < generation

    /**
     * Whether this player was last updated by something newer than [generation].
     */
    fun newerThan(generation: Long) = generation < this.generation

    /**
     * Shows [header] and [footer] to [player] unless they are already showing them, or unless an
     * update newer than [values] got there first.
     */
    fun send(
        player: TabViewer,
        values: TablistValues,
        header: Component,
        footer: Component
    ): Boolean = synchronized(this) {
        if (newerThan(values.generation)) return false

        generation = values.generation

        if (header == this.header && footer == this.footer) return false

        this.header = header
        this.footer = footer

        player.audience.sendPlayerListHeaderAndFooter(header, footer)

        true
    }
}
