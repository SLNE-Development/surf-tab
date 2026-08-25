package dev.slne.surf.tab.minestom

import com.google.inject.Inject
import com.google.inject.Singleton
import dev.slne.minestom.lobby.api.plugin.MinestomPluginEntrypoint
import dev.slne.minestom.lobby.api.plugin.annotation.DataDirectory
import dev.slne.surf.tab.core.client.config.tablistConfiguration
import dev.slne.surf.tab.core.client.hook.ClanHook
import dev.slne.surf.tab.core.client.hook.ContentCreatorHook
import dev.slne.surf.tab.core.client.redis.redisLoader
import dev.slne.surf.tab.core.client.service.TablistScheduler
import dev.slne.surf.tab.minestom.hook.registerLuckPermsListeners
import java.nio.file.Path

@Singleton
class TabMinestomEntrypoint @Inject constructor(
    @DataDirectory path: Path
) : MinestomPluginEntrypoint {

    init {
        dataPath = path
    }

    override suspend fun start() {
        tablistConfiguration.reload()

        redisLoader.onLoad()
        redisLoader.subscribeToEvents()
        redisLoader.onEnable()

        registerLuckPermsListeners()
        ClanHook.createListeners()
        ContentCreatorHook.registerListener()

        TablistScheduler.startTask()
    }

    override suspend fun stop() {
        TablistScheduler.cancelTask()
        redisLoader.disconnect()
    }

    companion object {
        @Volatile
        lateinit var dataPath: Path
            private set
    }
}
