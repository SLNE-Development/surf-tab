package dev.slne.surf.tab.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.tab.paper.plugin
import dev.slne.surf.tab.paper.service.tablistService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object ConnectionListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        tablistService.sendAdditions(event.player)

        plugin.launch {
            tablistService.formatPlayer(event.player)
        }
    }
}
