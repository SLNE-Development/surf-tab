package dev.slne.surf.tab.paper.listener

import dev.slne.surf.tab.core.client.service.tablistService
import dev.slne.surf.tab.paper.platform.PaperTabPlayer
import dev.slne.surf.vanish.api.event.PlayerNickEvent
import dev.slne.surf.vanish.api.event.PlayerReappearEvent
import dev.slne.surf.vanish.api.event.PlayerUnNickEvent
import dev.slne.surf.vanish.api.event.PlayerVanishEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object VanishListener : Listener {
    @EventHandler
    fun onVanish(event: PlayerVanishEvent) = requestFormat(event.player)

    @EventHandler
    fun onReappear(event: PlayerReappearEvent) = requestFormat(event.player)

    @EventHandler
    fun onNick(event: PlayerNickEvent) = requestFormat(event.player)

    @EventHandler
    fun onUnNick(event: PlayerUnNickEvent) = requestFormat(event.player)

    private fun requestFormat(player: Player?) {
        if (player == null) return

        tablistService.requestFormat(PaperTabPlayer(player))
    }
}
