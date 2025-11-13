package dev.slne.surf.tab.server

import dev.slne.surf.cloud.api.server.plugin.StandalonePlugin
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.tab.server.config.TablistConfigProvider
import dev.slne.surf.tab.server.placeholder.PlaceholderManager
import dev.slne.surf.tab.server.placeholder.impl.CloudPlaceholderExtension
import dev.slne.surf.tab.server.placeholder.impl.LuckPermsPlaceholderExtension

class ServerMain : StandalonePlugin() {
    val log = logger()

    override suspend fun load() {
    }

    override suspend fun enable() {
        PlaceholderManager.register(CloudPlaceholderExtension)
        PlaceholderManager.register(LuckPermsPlaceholderExtension)
    }

    override suspend fun disable() {
    }

    val configuration = TablistConfigProvider()
}

val config get() = plugin.configuration.config

val plugin get() = StandalonePlugin.getPlugin(ServerMain::class)