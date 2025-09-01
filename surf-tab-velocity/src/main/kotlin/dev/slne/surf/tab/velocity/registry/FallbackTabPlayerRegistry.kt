package dev.slne.surf.tab.velocity.registry

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import com.google.auto.service.AutoService
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.core.factory.tabPlayerFactory
import dev.slne.surf.tab.core.registry.TabPlayerRegistry
import dev.slne.surf.tab.velocity.plugin
import net.kyori.adventure.util.Services
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.jvm.optionals.getOrNull

@AutoService(TabPlayerRegistry::class)
class FallbackTabPlayerRegistry : TabPlayerRegistry, Services.Fallback {
    val playerCache: LoadingCache<UUID, TabPlayer> = Caffeine.newBuilder()
        .expireAfterWrite(20L, TimeUnit.MINUTES)
        .build {
            val velocityPlayer = plugin.proxy.getPlayer(it).getOrNull()
                ?: return@build tabPlayerFactory.createPlayer("Unknown", it)
            tabPlayerFactory.createPlayer(velocityPlayer)
        }

    override fun getTabPlayer(uuid: UUID): TabPlayer = playerCache[uuid]
}