package dev.slne.surf.tab.velocity.redis.event

import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class TabEntryUpdateRedisEvent(
    val toUpdateUuid: @Contextual UUID
) : RedisEvent()