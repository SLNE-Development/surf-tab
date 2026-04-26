package dev.slne.surf.tab.paper.service

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.tab.paper.*
import dev.slne.surf.tab.paper.hook.*
import dev.slne.surf.tab.paper.util.formatWithAdventure
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit

val tablistService = VelocityTablistService()

class VelocityTablistService {
    lateinit var task: ScheduledTask

    fun startTask() {
        task = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
            Bukkit.getOnlinePlayers().forEach { sendAdditions(it) }
        }, 0L, 1L, TimeUnit.SECONDS)
    }

    fun cancelTask() {
        if (::task.isInitialized) {
            task.cancel()
        }
    }

    fun sendAdditions(player: Player) {
        player.sendPlayerListHeaderAndFooter(
            tablistConfig.header.formatWithAdventure(player),
            tablistConfig.footer.formatWithAdventure(player)
        )
    }

    fun isAfk(playerUuid: UUID) = if (isPlaytimeHook) SurfPlaytimeHook.isAfk(playerUuid) else false
    fun isVanished(playerUuid: UUID) =
        if (isVanishHook) VanishHook.isVanished(playerUuid) else false

    suspend fun formatPlayer(player: Player) {
        player.playerListName(formatDisplayName(player))
        player.playerListOrder =
            if (isVanishHook && VanishHook.isVanished(player.uniqueId)) 0 else LuckPermsHook.getWeight(
                player.uniqueId
            )
    }

    private suspend fun formatDisplayName(player: Player) = buildText {
        append(getVanishTag(player.uniqueId))
        append(player.displayName())
        append(getClanTag(player.uniqueId))
        append(getLiveTag(player.uniqueId))
        append(getAfkTag(player.uniqueId))
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
        if (isContentCreatorHook) ContentCreatorHook.renderLiveTag(playerUuid) else Component.empty()

    private suspend fun getClanTag(playerUuid: UUID) = if (isClansHook) {
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