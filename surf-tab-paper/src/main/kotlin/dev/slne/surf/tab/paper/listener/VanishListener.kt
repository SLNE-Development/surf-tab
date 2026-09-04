package dev.slne.surf.tab.paper.listener

import dev.slne.surf.tab.core.client.service.TablistService
import dev.slne.surf.vanish.api.event.PlayerNickEvent
import dev.slne.surf.vanish.api.event.PlayerReappearEvent
import dev.slne.surf.vanish.api.event.PlayerUnNickEvent
import dev.slne.surf.vanish.api.event.PlayerVanishEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object VanishListener : Listener {
    @EventHandler
    fun onVanish(event: PlayerVanishEvent) = updateEntry(event.player)

    @EventHandler
    fun onReappear(event: PlayerReappearEvent) = updateEntry(event.player)

    @EventHandler
    fun onNick(event: PlayerNickEvent) = updateEntry(event.player)

    @EventHandler
    fun onUnNick(event: PlayerUnNickEvent) = updateEntry(event.player)

    private fun updateEntry(player: Player?) {
        if (player == null) return

        TablistService.updateEntry(player.uniqueId)
    }
}
