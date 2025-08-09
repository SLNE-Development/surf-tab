package dev.slne.surf.tab.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.tab.core.service.luckPermsService
import dev.slne.surf.tab.velocity.command.surfTabCommand
import dev.slne.surf.tab.velocity.config.TabConfigProvider
import dev.slne.surf.tab.velocity.listener.ConnectionListener
import dev.slne.surf.tab.velocity.util.papiProxyInstance

import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    @param:DataDirectory val dataPath: Path,
    suspendingPluginContainer: SuspendingPluginContainer
) {
    init {
        suspendingPluginContainer.initialize(this)
    }

    @Subscribe
    fun onInitialization(event: ProxyInitializeEvent) {
        INSTANCE = this
        surfTabCommand()

        luckPermsService.registerListener()
        papiProxyInstance.cacheExpiry =
            0L // This is intentionally set to 0 to disable caching. At the moment, the tablist is not updated dynamically, only on permission changes and connections.
        plugin.proxy.eventManager.register(plugin, ConnectionListener())
    }

    companion object {
        lateinit var INSTANCE: VelocityMain
            private set
    }
}

val plugin get() = VelocityMain.INSTANCE
val container
    get() = plugin.proxy.pluginManager.getPlugin("surf-tab-velocity").getOrNull()
        ?: error("The providing plugin container is not available. Got the plugin ID changed?")

val tabConfig = TabConfigProvider()