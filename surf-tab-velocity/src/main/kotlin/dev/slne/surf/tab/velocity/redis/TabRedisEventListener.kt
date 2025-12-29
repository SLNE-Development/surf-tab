package dev.slne.surf.tab.velocity.redis

import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.tab.api.redis.TabHideRedisEvent
import dev.slne.surf.tab.api.redis.TabShowRedisEvent
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.redis.event.TabEntryAddRedisEvent
import dev.slne.surf.tab.velocity.redis.event.TabEntryRemoveRedisEvent
import dev.slne.surf.tab.velocity.service.tablistService
import dev.slne.surf.tab.velocity.util.toVelocity
import kotlin.jvm.optionals.getOrNull

object TabRedisEventListener {
    @OnRedisEvent
    fun onTabEntryAdd(event: TabEntryAddRedisEvent) {
        val server = event.baseServer
        val seenServers = tablistService.getSeenServers(server)
        val visiblePlayers = seenServers.flatMap { it.playersConnected }

        seenServers.forEach {
            tablistService.entries[it] =
                tablistService.entries.getOrDefault(it, mutableListOf()) + event.tabEntry
        }

        visiblePlayers.forEach {
            tablistService.addPlayer(it, event.tabEntry)
        }
    }

    @OnRedisEvent
    fun onTabEntryRemove(event: TabEntryRemoveRedisEvent) {
        val server = event.baseServer
        val seenServers = tablistService.getSeenServers(server)
        val visiblePlayers = seenServers.flatMap { it.playersConnected }

        seenServers.forEach { server ->
            tablistService.entries[server]?.forEach {
                if (it.profile.uuid == event.profileUuid) {
                    tablistService.entries[server] =
                        tablistService.entries[server]?.filter { entry -> entry.profile.uuid != event.profileUuid }
                }
            }
        }

        visiblePlayers.forEach {
            tablistService.removePlayer(it, event.profileUuid)
        }
    }

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
}