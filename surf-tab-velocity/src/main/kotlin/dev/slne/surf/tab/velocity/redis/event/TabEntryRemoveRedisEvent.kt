package dev.slne.surf.tab.velocity.redis.event

import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.slne.surf.redis.event.RedisEvent
import dev.slne.surf.tab.velocity.serializer.RegisteredServerSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class TabEntryRemoveRedisEvent(
    val profileUuid: @Contextual UUID,
    @Serializable(with = RegisteredServerSerializer::class)
    val baseServer: RegisteredServer
) : RedisEvent()