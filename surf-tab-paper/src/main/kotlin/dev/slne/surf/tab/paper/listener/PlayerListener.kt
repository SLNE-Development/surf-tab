package dev.slne.surf.tab.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.tab.core.client.service.tablistService
import dev.slne.surf.tab.paper.platform.PaperTabPlayer
import dev.slne.surf.tab.paper.plugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerShowEntityEvent

object PlayerListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        tablistService.sendAdditions(PaperTabPlayer(event.player))

        plugin.launch {
            tablistService.formatPlayer(PaperTabPlayer(event.player))
        }
    }

    @EventHandler
    fun onShow(event: PlayerShowEntityEvent) {
        val target = event.entity as? Player ?: return

        plugin.launch {
            tablistService.formatPlayer(PaperTabPlayer(target))
        }
    }
}
