package dev.slne.surf.tab.paper.listener

import dev.slne.surf.playtime.api.paper.event.AfkStateChangeEvent
import dev.slne.surf.tab.paper.service.tablistService
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object PlaytimeListener : Listener {
    @EventHandler
    fun onAfkChange(event: AfkStateChangeEvent) {
        Bukkit.getPlayer(event.playerUuid)?.let {
            tablistService.formatPlayer(it)
        }
    }
}