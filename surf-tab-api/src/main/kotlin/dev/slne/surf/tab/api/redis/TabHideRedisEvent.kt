package dev.slne.surf.tab.api.redis

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class TabHideRedisEvent(
    val player: @Contextual UUID,
    val toHide: @Contextual UUID
)
