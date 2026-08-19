package dev.slne.surf.tab.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.tab.core.client.service.tablistService
import dev.slne.surf.tab.paper.platform.PaperTabPlayer
import dev.slne.surf.tab.paper.plugin
import dev.slne.surf.vanish.api.event.PlayerNickEvent
import dev.slne.surf.vanish.api.event.PlayerReappearEvent
import dev.slne.surf.vanish.api.event.PlayerUnNickEvent
import dev.slne.surf.vanish.api.event.PlayerVanishEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object VanishListener : Listener {
    @EventHandler
    fun onVanish(event: PlayerVanishEvent) {
        plugin.launch {
            event.player?.let {
                tablistService.formatPlayer(PaperTabPlayer(it))
            }
        }
    }

    @EventHandler
    fun onReappear(event: PlayerReappearEvent) {
        plugin.launch {
            event.player?.let {
                tablistService.formatPlayer(PaperTabPlayer(it))
            }
        }
    }

    @EventHandler
    fun onNick(event: PlayerNickEvent) {
        plugin.launch {
            event.player?.let {
                tablistService.formatPlayer(PaperTabPlayer(it))
            }
        }
    }

    @EventHandler
    fun onUnNick(event: PlayerUnNickEvent) {
        plugin.launch {
            event.player?.let {
                tablistService.formatPlayer(PaperTabPlayer(it))
            }
        }
    }
}
