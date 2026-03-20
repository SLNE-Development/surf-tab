package dev.slne.surf.tab.paper.listener

import dev.slne.surf.playtime.api.paper.event.AfkStateChangeEvent
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.tab.paper.plugin
import dev.slne.surf.tab.paper.service.tablistService
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object PlaytimeListener : Listener {
    @EventHandler
    fun onAfkChange(event: AfkStateChangeEvent) {
        Bukkit.getPlayer(event.playerUuid)?.let {
            plugin.launch {
                tablistService.formatPlayer(it)
            }
        }
    }
}