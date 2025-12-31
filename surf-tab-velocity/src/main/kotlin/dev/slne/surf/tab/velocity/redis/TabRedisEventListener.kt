package dev.slne.surf.tab.velocity.redis

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.player.TabListEntry
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.tab.api.redis.TabHideRedisEvent
import dev.slne.surf.tab.api.redis.TabShowRedisEvent
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.redis.event.TabEntryAddRedisEvent
import dev.slne.surf.tab.velocity.redis.event.TabEntryRemoveRedisEvent
import dev.slne.surf.tab.velocity.redis.event.TabEntryUpdateRedisEvent
import dev.slne.surf.tab.velocity.service.tablistService
import java.util.*
import kotlin.jvm.optionals.getOrNull

object TabRedisEventListener {
    private val hiddenEntries = mutableObject2ObjectMapOf<UUID, List<TabListEntry>>()

    @OnRedisEvent
    fun onTabHide(event: TabHideRedisEvent) {
        val player = plugin.proxy.getPlayer(event.player).getOrNull() ?: return

        if (!player.tabList.containsEntry(event.toHide)) {
            return
        }

        val entry = player.tabList.getEntry(event.toHide).getOrNull() ?: return
        hiddenEntries[event.player] = (hiddenEntries[event.player] ?: mutableListOf()) + entry

        player.tabList.removeEntry(event.toHide)
    }

    @OnRedisEvent
    fun onTabShow(event: TabShowRedisEvent) {
        val player = plugin.proxy.getPlayer(event.player).getOrNull() ?: return
        val entry = hiddenEntries[event.player]?.find { it.profile.id == event.toShow } ?: return

        hiddenEntries[event.player] =
            hiddenEntries[event.player]?.filterNot { it.profile.id == event.toShow }
                ?: mutableListOf()

        player.tabList.addEntry(entry)
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