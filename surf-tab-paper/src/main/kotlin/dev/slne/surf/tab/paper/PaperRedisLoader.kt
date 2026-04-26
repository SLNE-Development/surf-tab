package dev.slne.surf.tab.paper

import dev.slne.surf.redis.RedisApi
import dev.slne.surf.tab.paper.redis.TabRedisEventListener

val redisLoader = BukkitRedisLoader()
val redisApi get() = redisLoader.redisApi

class BukkitRedisLoader {
    lateinit var redisApi: RedisApi

    fun connect() {
        redisApi = RedisApi.create()
        redisApi.subscribeToEvents(TabRedisEventListener)
        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}