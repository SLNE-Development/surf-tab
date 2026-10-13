package dev.slne.surf.tab.minestom

import dev.slne.surf.api.minestom.plugin.SurfMinestomPlugin
import dev.slne.surf.tab.core.client.hook.ClanHook
import dev.slne.surf.tab.core.client.hook.ContentCreatorHook
import dev.slne.surf.tab.core.client.redis.redisLoader
import dev.slne.surf.tab.core.client.service.TablistService
import dev.slne.surf.tab.minestom.command.surfTabCommand
import dev.slne.surf.tab.minestom.hook.registerLuckPermsListeners
import dev.slne.surf.tab.minestom.listener.PlayerListener
import dev.slne.surf.tab.minestom.listener.PlaytimeListener
import dev.slne.surf.tab.minestom.platform.MinestomTabPlatform

object TabMinestomPlugin : SurfMinestomPlugin() {
    override suspend fun onLoad() {
        redisLoader.onLoad()
        redisLoader.subscribeToEvents()
        redisLoader.onEnable()

        TablistService.start(MinestomTabPlatform())

        registerLuckPermsListeners()
        ClanHook.createListeners()
        ContentCreatorHook.registerListener()
    }

    override suspend fun onEnable() {
        surfTabCommand()

        PlayerListener.register(eventNode)
        PlaytimeListener.register(eventNode)
    }

    override suspend fun onDisable() {
        TablistService.stop()
        redisLoader.disconnect()
    }
}
