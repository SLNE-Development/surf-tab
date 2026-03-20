package dev.slne.surf.tab.paper.redis

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.tab.api.redis.TabEntryUpdateRedisEvent
import dev.slne.surf.tab.paper.plugin
import dev.slne.surf.tab.paper.service.tablistService
import dev.slne.surf.vanish.api.redis.VanishStateUpdateRedisEvent
import org.bukkit.Bukkit

object TabRedisEventListener {
    @OnRedisEvent
    fun onUpdate(event: TabEntryUpdateRedisEvent) {
        Bukkit.getPlayer(event.toUpdateUuid)?.let {
            plugin.launch {
                tablistService.formatPlayer(it)
            }
        }
    }

    @OnRedisEvent
    fun onVanishUpdate(event: VanishStateUpdateRedisEvent) {
        Bukkit.getPlayer(event.player)?.let {
            plugin.launch {
                tablistService.formatPlayer(it)
            }
        }
    }
}