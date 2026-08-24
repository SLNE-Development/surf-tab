package dev.slne.surf.tab.paper.listener

import dev.slne.surf.tab.core.client.service.tablistService
import dev.slne.surf.tab.paper.platform.PaperTabPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerShowEntityEvent

object PlayerListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = PaperTabPlayer(event.player)

        tablistService.sendAdditions(player)
        tablistService.requestFormat(player)
    }

    @EventHandler
    fun onShow(event: PlayerShowEntityEvent) {
        val target = event.entity as? Player ?: return

        tablistService.requestFormat(PaperTabPlayer(target))
    }
}
