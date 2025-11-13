package dev.slne.surf.tab.server

import dev.slne.surf.cloud.api.server.plugin.StandalonePlugin
import dev.slne.surf.surfapi.core.api.util.logger

class ServerMain(
    private val syncService: ServerSyncService
) : StandalonePlugin() {
    val log = logger()

    override suspend fun load() {
    }

    override suspend fun enable() {
        log.atInfo().log("")
    }

    override suspend fun disable() {
    }
}

val plugin get() = StandalonePlugin.getPlugin(ServerMain::class)