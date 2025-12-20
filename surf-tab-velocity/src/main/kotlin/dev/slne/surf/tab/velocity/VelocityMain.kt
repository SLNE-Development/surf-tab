package dev.slne.surf.tab.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.tab.velocity.command.surfTabCommand
import dev.slne.surf.tab.velocity.config.TablistConfigProvider
import dev.slne.surf.tab.velocity.hook.LuckPermsHook
import dev.slne.surf.tab.velocity.listener.ConnectionListener
import org.slf4j.Logger
import java.nio.file.Path

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    @param:DataDirectory val dataPath: Path,
    suspendingPluginContainer: SuspendingPluginContainer,
    val pluginContainer: PluginContainer,
    val logger: Logger
) {
    init {
        suspendingPluginContainer.initialize(this)
    }

    @Subscribe
    fun onInitialization(event: ProxyInitializeEvent) {
        instance = this

        surfTabCommand()
        LuckPermsHook.load()

        plugin.proxy.eventManager.register(plugin, ConnectionListener())
    }

    companion object {
        lateinit var instance: VelocityMain
    }
}

val tablistConfiguration = TablistConfigProvider()
val tablistConfig get() = tablistConfiguration.config
val plugin get() = VelocityMain.instance