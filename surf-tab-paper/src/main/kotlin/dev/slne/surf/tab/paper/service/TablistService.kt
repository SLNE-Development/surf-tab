package dev.slne.surf.tab.paper.service

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.tab.paper.*
import dev.slne.surf.tab.paper.hook.ClanHook
import dev.slne.surf.tab.paper.hook.LuckPermsHook
import dev.slne.surf.tab.paper.hook.SurfPlaytimeHook
import dev.slne.surf.tab.paper.hook.SurfVanishHook
import dev.slne.surf.tab.paper.util.formatWithAdventure
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
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
        if (isVanishHook) SurfVanishHook.isVanished(playerUuid) else false

    suspend fun formatPlayer(player: Player) {
        player.playerListName(formatDisplayName(player))
        player.playerListOrder = LuckPermsHook.getWeight(player.uniqueId)
    }

    private suspend fun formatDisplayName(player: Player) = buildText {
        append(getVanishTag(player.uniqueId))
        append(
            MiniMessage.miniMessage().deserialize(
                LuckPermsHook.getPrefix(player.uniqueId) + player.name + LuckPermsHook.getSuffix(
                    player.uniqueId
                )
            )
        )
        append(getClanTag(player.uniqueId))
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

    private suspend fun getClanTag(playerUuid: UUID) = if (isClansHook) {
        buildText {
            val tag = ClanHook.getClanTag(playerUuid)

            if (tag != null) {
                appendSpace()
                append(tag)
            }
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