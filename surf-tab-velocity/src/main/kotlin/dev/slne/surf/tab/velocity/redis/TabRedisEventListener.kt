package dev.slne.surf.tab.velocity.redis

import dev.slne.redis.event.OnRedisEvent
import dev.slne.surf.tab.velocity.redis.event.TabEntryAddRedisEvent
import dev.slne.surf.tab.velocity.redis.event.TabEntryRemoveRedisEvent
import dev.slne.surf.tab.velocity.service.tablistService

object TabRedisEventListener {
    @OnRedisEvent
    fun onTabEntryAdd(event: TabEntryAddRedisEvent) {
        val server = event.baseServer
        val seenServers = tablistService.getSeenServers(server)
        val visiblePlayers = seenServers.flatMap { it.playersConnected }

        tablistService.entries.add(event.tabEntry)

        visiblePlayers.forEach {
            tablistService.addPlayer(it, event.tabEntry)
        }
    }

    @OnRedisEvent
    fun onTabEntryRemove(event: TabEntryRemoveRedisEvent) {
        println("Removing tab entry for UUID ${event.profileUuid} from tablist via Redis event")
        val server = event.baseServer
        val seenServers = tablistService.getSeenServers(server)
        val visiblePlayers = seenServers.flatMap { it.playersConnected }

        tablistService.entries.removeIf { it.profile.uuid == event.profileUuid }

        visiblePlayers.forEach {
            tablistService.removePlayer(it, event.profileUuid)
        }
    }
}