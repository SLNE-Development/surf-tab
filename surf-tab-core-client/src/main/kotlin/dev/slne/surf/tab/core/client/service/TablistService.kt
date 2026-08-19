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
import net.kyori.adventure.text.Component
import java.util.*

val tablistService = TablistService()

class TablistService {

    fun sendAdditionsToAll() {
        TabPlatform.onlinePlayers().forEach { sendAdditions(it) }
    }

    fun sendAdditions(player: TabPlayer) {
        player.audience.sendPlayerListHeaderAndFooter(
            tablistConfig.header.formatWithAdventure(player),
            tablistConfig.footer.formatWithAdventure(player)
        )
    }

    fun isAfk(playerUuid: UUID) =
        TabPlatform.playtimeAvailable && SurfPlaytimeHook.isAfk(playerUuid)

    fun isVanished(playerUuid: UUID) = TabPlatform.isVanished(playerUuid)

    suspend fun formatPlayer(player: TabPlayer) {
        player.showTabName(formatDisplayName(player))
        player.showTabOrder(LuckPermsHook.getWeight(player.uuid))
    }

    private suspend fun formatDisplayName(player: TabPlayer) = buildText {
        append(getVanishTag(player.uuid))
        append(player.baseName())
        append(getClanTag(player.uuid))
        append(getLiveTag(player.uuid))
        append(getAfkTag(player.uuid))
    }

    private fun getAfkTag(playerUuid: UUID) = if (isAfk(playerUuid)) {
        buildText {
            appendSpace()
            darkSpacer("[")
            spacer("AFK")
            darkSpacer("]")
        }
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
        buildText {
            darkSpacer("[")
            note("V")
            darkSpacer("]")
            appendSpace()
        }
    } else {
        Component.empty()
    }
}
