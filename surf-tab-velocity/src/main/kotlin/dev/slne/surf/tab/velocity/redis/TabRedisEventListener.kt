package dev.slne.surf.tab.velocity.redis

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.player.TabListEntry
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.tab.api.redis.TabEntryUpdateRedisEvent
import dev.slne.surf.tab.api.redis.TabHideRedisEvent
import dev.slne.surf.tab.api.redis.TabShowRedisEvent
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.redis.event.TabEntryAddRedisEvent
import dev.slne.surf.tab.velocity.redis.event.TabEntryRemoveRedisEvent
import dev.slne.surf.tab.velocity.service.tablistService
import java.util.*
import kotlin.jvm.optionals.getOrNull

object TabRedisEventListener {
    private val hiddenEntries = mutableObject2ObjectMapOf<UUID, List<TabListEntry>>()

    @OnRedisEvent
    fun onTabHide(event: TabHideRedisEvent) {
        println("Received TabHideRedisEvent for player ${event.player} to hide ${event.toHide}")
        val player = plugin.proxy.getPlayer(event.player).getOrNull() ?: return
        println("Found player ${player.username}")

        if (!player.tabList.containsEntry(event.toHide)) {
            println("Player's tab list does not contain entry ${event.toHide}, nothing to hide")
            return
        }

        println("Hiding entry ${event.toHide} from player ${player.username}'s tab list")
        val entry = player.tabList.getEntry(event.toHide).getOrNull() ?: return
        println("Found entry ${entry.profile.name} to hide")
        hiddenEntries[event.player] = (hiddenEntries[event.player] ?: mutableListOf()) + entry
        println("Stored hidden entry for player ${player.username}")

        player.tabList.removeEntry(event.toHide)
    }

    @OnRedisEvent
    fun onTabShow(event: TabShowRedisEvent) {
        println("Received TabShowRedisEvent for player ${event.player} to show ${event.toShow}")
        val player = plugin.proxy.getPlayer(event.player).getOrNull() ?: return
        println("Found player ${player.username}")
        val entry = hiddenEntries[event.player]?.find { it.profile.id == event.toShow } ?: return
        println("Showing entry ${entry.profile.name} to player ${player.username}")

        hiddenEntries[event.player] =
            hiddenEntries[event.player]?.filterNot { it.profile.id == event.toShow }
                ?: mutableListOf()
        println("Removed entry from hidden entries for player ${player.username}")

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