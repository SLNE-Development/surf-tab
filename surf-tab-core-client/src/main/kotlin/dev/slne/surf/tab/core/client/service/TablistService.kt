package dev.slne.surf.tab.core.client.service

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.tab.core.client.config.tablistConfig
import dev.slne.surf.tab.core.client.hook.ClanHook
import dev.slne.surf.tab.core.client.hook.ContentCreatorHook
import dev.slne.surf.tab.core.client.hook.LuckPermsHook
import dev.slne.surf.tab.core.client.hook.SurfPlaytimeHook
import dev.slne.surf.tab.core.client.platform.TabPlatform
import dev.slne.surf.tab.core.client.platform.TabPlayer
import dev.slne.surf.tab.core.client.util.formatWithAdventure
import dev.slne.surf.tab.core.client.util.tablistPlaceholders
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.*

val tablistService = TablistService()

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

    private val updates = UpdateCoalescer<TabPlayer>(
        runUpdates = { block -> TabPlatform.launch { block() } },
        update = { player -> formatPlayer(player) }
    )

    /**
     * Sends the header and footer to everyone currently online.
     */
    fun sendAdditionsToAll() = sendAdditions(TabPlatform.onlinePlayers())

    /**
     * Re-sends the header, the footer and the entry of everyone currently online, as a configuration
     * reload has to.
     */
    fun refreshAll() {
        val players = TabPlatform.onlinePlayers()

        sendAdditions(players)

        for (player in players) {
            requestFormat(player)
        }
    }

    fun sendAdditions(player: TabPlayer) {
        val config = tablistConfig

        sendAdditions(
            player,
            config.header,
            config.footer,
            tablistPlaceholders(TabPlatform.onlinePlayerCount())
        )
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

    private suspend fun formatPlayer(player: TabPlayer) {
        player.showTabEntry(formatDisplayName(player), LuckPermsHook.getWeight(player.uuid))
    }

    private fun sendAdditions(players: Collection<TabPlayer>) {
        val config = tablistConfig
        val header = config.header
        val footer = config.footer
        val placeholders = tablistPlaceholders(players.size)

        for (player in players) {
            sendAdditions(player, header, footer, placeholders)
        }
    }

    private fun sendAdditions(
        player: TabPlayer,
        header: String,
        footer: String,
        placeholders: TagResolver
    ) {
        player.audience.sendPlayerListHeaderAndFooter(
            header.formatWithAdventure(player, placeholders),
            footer.formatWithAdventure(player, placeholders)
        )
    }

    private suspend fun formatDisplayName(player: TabPlayer) = buildText {
        append(getVanishTag(player.uuid))
        append(player.baseName())
        append(getClanTag(player.uuid))
        append(getLiveTag(player.uuid))
        append(getAfkTag(player.uuid))
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

    private suspend fun getClanTag(playerUuid: UUID) = if (TabPlatform.clanAvailable) {
        val tag = ClanHook.getClanTag(playerUuid)
        if (tag != null) {
            buildText {
                appendSpace()
                append(tag)
            }
        } else {
            Component.empty()
        }
    } else {
        Component.empty()
    }

    private fun getVanishTag(playerUuid: UUID) = if (isVanished(playerUuid)) {
        vanishTag
    } else {
        Component.empty()
    }
}
