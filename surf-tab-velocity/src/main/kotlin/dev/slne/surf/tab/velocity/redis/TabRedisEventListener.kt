package dev.slne.surf.tab.velocity.redis

import dev.slne.redis.event.OnRedisEvent
import dev.slne.surf.tab.velocity.redis.event.TabEntryAddRedisEvent
import dev.slne.surf.tab.velocity.service.tablistService

class TabRedisEventListener {
    @OnRedisEvent
    fun onTabEntryAdd(event: TabEntryAddRedisEvent) {
        val server = event.baseServer
        val seenServers = tablistService.getSeenServers(server)
        val visiblePlayers = seenServers.flatMap { it.playersConnected }

        visiblePlayers.forEach { other ->
            tablistService.addPlayer(
                player,
                tablistService.createEntry(other, player)
            )
        }

        visiblePlayers.forEach { other ->
            tablistService.addPlayer(
                other,
                tablistService.createEntry(player, other)
            )
        }
    }
}