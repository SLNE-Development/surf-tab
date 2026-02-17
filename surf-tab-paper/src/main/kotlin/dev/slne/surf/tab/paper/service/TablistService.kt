package dev.slne.surf.tab.paper.service

import dev.slne.surf.tab.paper.hook.LuckPermsHook
import dev.slne.surf.tab.paper.plugin
import dev.slne.surf.tab.paper.tablistConfig
import dev.slne.surf.tab.paper.util.formatWithAdventure
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.entity.Player
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

    fun formatPlayer(player: Player) {
        player.playerListName(formatDisplayName(player))
        player.playerListOrder = LuckPermsHook.getWeight(player.uniqueId)
    }

    private fun formatDisplayName(player: Player) =
        tablistConfig.playerName.formatWithAdventure(player)
}