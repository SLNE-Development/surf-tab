package dev.slne.surf.tab.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.playtime.api.paper.event.AfkStateChangeEvent
import dev.slne.surf.tab.core.client.service.tablistService
import dev.slne.surf.tab.paper.platform.PaperTabPlayer
import dev.slne.surf.tab.paper.plugin
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object PlaytimeListener : Listener {
    @EventHandler
    fun onAfkChange(event: AfkStateChangeEvent) {
        Bukkit.getPlayer(event.playerUuid)?.let {
            plugin.launch {
                tablistService.formatPlayer(PaperTabPlayer(it))
            }
        }
    }
}
