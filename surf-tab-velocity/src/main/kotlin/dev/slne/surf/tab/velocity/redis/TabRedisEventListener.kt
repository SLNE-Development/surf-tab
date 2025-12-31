package dev.slne.surf.tab.velocity.redis

import com.github.shynixn.mccoroutine.velocity.launch
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.tab.api.redis.TabHideRedisEvent
import dev.slne.surf.tab.api.redis.TabShowRedisEvent
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.redis.event.TabEntryAddRedisEvent
import dev.slne.surf.tab.velocity.redis.event.TabEntryRemoveRedisEvent
import dev.slne.surf.tab.velocity.redis.event.TabEntryUpdateRedisEvent
import dev.slne.surf.tab.velocity.service.tablistService
import dev.slne.surf.tab.velocity.util.toVelocity
import kotlin.jvm.optionals.getOrNull

object TabRedisEventListener {
    @OnRedisEvent
    fun onTabHide(event: TabHideRedisEvent) {
        val player = plugin.proxy.getPlayer(event.player).getOrNull() ?: return
        player.tabList.removeEntry(event.toHide)
    }

    @OnRedisEvent
    fun onTabShow(event: TabShowRedisEvent) {
        val player = plugin.proxy.getPlayer(event.player).getOrNull() ?: return
        val server = player.currentServer.getOrNull()?.server ?: return
        val entry =
            tablistService.entries[server]?.find { it.profile.uuid == event.toShow } ?: return

        player.tabList.addEntry(entry.toVelocity(player.tabList))// TODO: Not working with multiproxy support
    }

    @OnRedisEvent
    fun onAdd(event: TabEntryAddRedisEvent) {
        plugin.pluginContainer.launch {
            plugin.proxy.allPlayers.forEach {
                tablistService.formatNewPlayer(it, event.toAddUuid)
            }
        }
    }

    @OnRedisEvent
    fun onRemove(event: TabEntryRemoveRedisEvent) {
        plugin.proxy.allPlayers.forEach {
            tablistService.formatLostPlayer(it, event.toRemoveUuid)
        }
    }

    @OnRedisEvent
    fun onUpdate(event: TabEntryUpdateRedisEvent) {
        plugin.pluginContainer.launch {
            tablistService.reformatPlayerForOnlinePlayers(event.toUpdateUuid)
        }
    }
}