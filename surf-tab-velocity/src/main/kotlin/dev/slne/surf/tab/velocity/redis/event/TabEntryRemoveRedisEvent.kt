package dev.slne.surf.tab.velocity.redis.event

import dev.slne.redis.event.RedisEvent
import dev.slne.surf.tab.velocity.serializer.SerializableRegisteredServer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class TabEntryRemoveRedisEvent(
    val profileUuid: @Contextual UUID,
    val baseServer: SerializableRegisteredServer
) : RedisEvent()