package dev.slne.surf.tab.velocity.redis.event

import dev.slne.redis.event.RedisEvent
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.velocity.serializer.SerializableRegisteredServer

data class TabEntryRemoveRedisEvent(
    val tabEntry: TabEntry,
    val baseServer: SerializableRegisteredServer
) : RedisEvent()