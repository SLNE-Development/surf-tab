package dev.slne.surf.tab.paper.listener

import dev.slne.surf.tab.core.client.service.TablistUpdateReason
import dev.slne.surf.tab.core.client.service.TablistService
import dev.slne.surf.tab.paper.platform.PaperTabPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerShowEntityEvent

object PlayerListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = PaperTabPlayer(event.player)

        TablistService.invalidatePlayer(player)
        TablistService.requestFormat(player)
        TablistService.invalidateAll(TablistUpdateReason.PLAYER_COUNT)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        TablistService.forget(event.player.uniqueId)
        TablistService.invalidateAll(TablistUpdateReason.PLAYER_COUNT)
    }

    @EventHandler
    fun onShow(event: PlayerShowEntityEvent) {
        val target = event.entity as? Player ?: return

        TablistService.requestFormat(PaperTabPlayer(target))
    }
}
