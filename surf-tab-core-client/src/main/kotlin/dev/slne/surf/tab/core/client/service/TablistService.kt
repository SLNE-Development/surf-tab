package dev.slne.surf.tab.core.client.service

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.api.core.util.logger
import dev.slne.surf.core.api.common.server.SurfServer
import dev.slne.surf.tab.core.client.config.tablistConfig
import dev.slne.surf.tab.core.client.hook.ClanHook
import dev.slne.surf.tab.core.client.hook.ContentCreatorHook
import dev.slne.surf.tab.core.client.hook.LuckPermsHook
import dev.slne.surf.tab.core.client.hook.SurfPlaytimeHook
import dev.slne.surf.tab.core.client.platform.TabPlatform
import dev.slne.surf.tab.core.client.platform.TabPlayer
import dev.slne.surf.tab.core.client.util.AdventureTablistRenderer
import dev.slne.surf.tab.core.client.util.formatTablistDate
import dev.slne.surf.tab.core.client.util.formatTablistTime
import net.kyori.adventure.text.Component
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration.Companion.seconds

val tablistService = TablistService()

private val log = logger()

private val tabEntryUpdateTimeout = 5.seconds
private val clanLookupTimeout = 2.seconds

private val afkTag = buildText {
    appendSpace()
    darkSpacer("[")
    spacer("AFK")
    darkSpacer("]")
}

private val vanishTag = buildText {
    darkSpacer("[")
    note("V")
    darkSpacer("]")
    appendSpace()
}

class TablistService {

    private val entryUpdater = TabEntryUpdater<TabPlayer>(
        baseName = { player -> player.baseNameSnapshot() },
        order = { player -> LuckPermsHook.getWeight(player.uuid) },
        vanishTag = { player -> getVanishTag(player.uuid) },
        clanTag = { player -> getClanTag(player.uuid) },
        liveTag = { player -> getLiveTag(player.uuid) },
        afkTag = { player -> getAfkTag(player.uuid) },
        show = { player, name, order -> player.showTabEntry(name, order) },
        clanTimeout = clanLookupTimeout,
        onPartFailure = { part, player, failure ->
            log.atWarning()
                .withCause(failure)
                .log("Failed to resolve tablist part %s for player %s", part, player.uuid)
        }
    )

    private val updates = UpdateCoalescer(
        runUpdates = { block -> TabPlatform.launch { block() } },
        updateTimeout = tabEntryUpdateTimeout,
        onTimeout = { playerUuid, _ ->
            log.atWarning().log("Tablist update for player %s timed out", playerUuid)
        },
        update = entryUpdater::update
    )

    /**
     * Counts the snapshots of [TablistValues] up, so that a later one is recognisable as the newer
     * one no matter which thread took it.
     */
    private val generations = AtomicLong()

    /**
     * The analysed form of the configured templates.
     */
    @Volatile
    private var analyzed: TablistTemplates? = null

    private val additions = TablistAdditions(
        templates = { templates() },
        captureValues = { captureValues() },
        onlinePlayers = { TabPlatform.onlinePlayers() },
        onlinePlayerCount = { TabPlatform.onlinePlayerCount() },
        renderer = AdventureTablistRenderer,
        runUpdates = { block -> TabPlatform.launch { block() } }
    )

    /**
     * The currently configured templates, analysing them again if the configuration moved on.
     */
    fun templates(): TablistTemplates {
        val config = tablistConfig
        val current = analyzed

        if (current != null && current.matches(config.header, config.footer)) return current

        val analyzed = TablistTemplates.analyze(config.header, config.footer, miniMessage)
        this.analyzed = analyzed

        return analyzed
    }

    /**
     * Asks for everybody's header and footer to be brought up to date because of [reason].
     */
    fun invalidateAll(reason: TablistUpdateReason) = additions.invalidateAll(reason)

    /**
     * Brings [player]'s header and footer up to date right away, for a player who cannot wait for
     * the next update of everybody because they have nothing yet.
     */
    fun invalidatePlayer(player: TabPlayer) = additions.invalidatePlayer(player)

    /**
     * Forgets everything remembered about [playerUuid], because they left.
     */
    fun forget(playerUuid: UUID) = additions.forget(playerUuid)

    /**
     * Re-sends the header, the footer and the entry of everyone currently online, as a configuration
     * reload has to.
     */
    fun refreshAll() {
        invalidateAll(TablistUpdateReason.CONFIGURATION)

        for (player in TabPlatform.onlinePlayers()) {
            requestFormat(player)
        }
    }

    /**
     * Asks for [player]'s tablist entry to be rebuilt.
     *
     * Rebuilds of the same player are collapsed and run one after another, so this can be called as
     * often as any listener fires. Safe to call from any thread.
     */
    fun requestFormat(player: TabPlayer) = updates.request(player.uuid, player)

    fun isAfk(playerUuid: UUID) =
        TabPlatform.playtimeAvailable && SurfPlaytimeHook.isAfk(playerUuid)

    fun isVanished(playerUuid: UUID) = TabPlatform.isVanished(playerUuid)

    /**
     * Reads everything the tablist fills its own placeholders with, once, for a whole update.
     */
    private fun captureValues(): TablistValues {
        val now = ZonedDateTime.now()

        return TablistValues(
            generation = generations.incrementAndGet(),
            server = SurfServer.current().name,
            onlinePlayers = TabPlatform.onlinePlayerCount(),
            maxPlayers = TabPlatform.maxPlayerCount(),
            date = formatTablistDate(now),
            time = formatTablistTime(now)
        )
    }

    private fun getAfkTag(playerUuid: UUID) = if (isAfk(playerUuid)) {
        afkTag
    } else {
        Component.empty()
    }

    private fun getLiveTag(playerUuid: UUID) =
        if (TabPlatform.contentCreatorAvailable) {
            ContentCreatorHook.renderLiveTag(playerUuid)
        } else {
            Component.empty()
        }

    private suspend fun getClanTag(playerUuid: UUID): Component? {
        if (!TabPlatform.clanAvailable) return null

        val tag = ClanHook.getClanTag(playerUuid) ?: return null

        return buildText {
            appendSpace()
            append(tag)
        }
    }

    private fun getVanishTag(playerUuid: UUID) = if (isVanished(playerUuid)) {
        vanishTag
    } else {
        Component.empty()
    }
}
