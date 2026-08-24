package dev.slne.surf.tab.core.client.redis

import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.tab.api.redis.TabEntryUpdateRedisEvent
import dev.slne.surf.tab.core.client.platform.TabPlatform
import dev.slne.surf.tab.core.client.service.tablistService

object TabRedisEventListener {
    @OnRedisEvent
    fun onUpdate(event: TabEntryUpdateRedisEvent) {
        TabPlatform.player(event.toUpdateUuid)?.let {
            tablistService.requestFormat(it)
        }
    }
}
