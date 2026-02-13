package dev.slne.surf.tab.paper

import dev.slne.surf.redis.RedisApi
import dev.slne.surf.redis.sync.set.SyncSet
import dev.slne.surf.tab.paper.redis.TabRedisEventListener
import java.util.*

val redisLoader = BukkitRedisLoader()
val redisApi get() = redisLoader.redisApi

class BukkitRedisLoader {
    lateinit var redisApi: RedisApi

    lateinit var afkPlayers: SyncSet<UUID>
    lateinit var vanishedPlayers: SyncSet<UUID>

    fun connect() {
        redisApi = RedisApi.create()

        afkPlayers = redisApi.createSyncSet("surf-playtime:afk-players")
        vanishedPlayers = redisApi.createSyncSet("vanished_players")

        redisApi.subscribeToEvents(TabRedisEventListener)
        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}