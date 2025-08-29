package dev.slne.surf.tab.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.tab.core.service.luckPermsService
import dev.slne.surf.tab.core.service.tabGroupService
import dev.slne.surf.tab.velocity.command.surfTabCommand
import dev.slne.surf.tab.velocity.config.TabConfigProvider
import dev.slne.surf.tab.velocity.config.TabGroupConfigProvider
import dev.slne.surf.tab.velocity.listener.ConnectionListener
import java.nio.file.Path

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    val pluginContainer: PluginContainer,
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

        tabGroupService.loadGroups(true)

        luckPermsService.registerListener()
        plugin.proxy.eventManager.register(plugin, ConnectionListener())
    }

    companion object {
        lateinit var INSTANCE: VelocityMain
            private set
    }
}

val plugin get() = VelocityMain.INSTANCE
val container = VelocityMain.INSTANCE.pluginContainer

val tabConfig = TabConfigProvider()
val tabGroupConfig = TabGroupConfigProvider()