package dev.slne.surf.tab.paper.listener

import dev.slne.surf.playtime.api.paper.event.AfkStateChangeEvent
import dev.slne.surf.tab.core.client.service.tablistService
import dev.slne.surf.tab.paper.platform.PaperTabPlayer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object PlaytimeListener : Listener {
    @EventHandler
    fun onAfkChange(event: AfkStateChangeEvent) {
        val player = Bukkit.getPlayer(event.playerUuid) ?: return

        tablistService.requestFormat(PaperTabPlayer(player))
    }
}
