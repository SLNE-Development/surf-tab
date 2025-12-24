package dev.slne.surf.tab.velocity.redis.event

import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.slne.surf.redis.event.RedisEvent
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.velocity.serializer.RegisteredServerSerializer
import kotlinx.serialization.Serializable

@Serializable
data class TabEntryAddRedisEvent(
    val tabEntry: TabEntry,
    @Serializable(with = RegisteredServerSerializer::class)
    val baseServer: RegisteredServer
) : RedisEvent()