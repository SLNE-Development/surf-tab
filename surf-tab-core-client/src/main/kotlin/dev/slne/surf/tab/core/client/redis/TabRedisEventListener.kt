package dev.slne.surf.tab.core.client.redis

import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.tab.api.redis.TabEntryUpdateRedisEvent
import dev.slne.surf.tab.core.client.service.TablistService

object TabRedisEventListener {
    @OnRedisEvent
    fun onUpdate(event: TabEntryUpdateRedisEvent) {
        TablistService.updateEntry(event.toUpdateUuid)
    }
}
