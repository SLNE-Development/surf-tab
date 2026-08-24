package dev.slne.surf.tab.core.client.redis

import dev.slne.surf.redis.RedisApi

val redisLoader = TabRedisLoader()
val redisApi get() = redisLoader.redisApi

class TabRedisLoader {
    @Volatile
    lateinit var redisApi: RedisApi
        private set

    fun onLoad() {
        redisApi = RedisApi.create()
    }

    fun subscribeToEvents() {
        redisApi.subscribeToEvents(TabRedisEventListener)
    }

    fun onEnable() {
        redisApi.freezeAndConnect()
    }

    fun connect() {
        onLoad()
        subscribeToEvents()
        onEnable()
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}
