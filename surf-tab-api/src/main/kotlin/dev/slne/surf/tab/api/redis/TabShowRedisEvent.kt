package dev.slne.surf.tab.api.redis

import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class TabShowRedisEvent(
    val player: @Contextual UUID,
    val toShow: @Contextual UUID
) : RedisEvent()
