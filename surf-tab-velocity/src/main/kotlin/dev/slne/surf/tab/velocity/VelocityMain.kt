package dev.slne.surf.tab.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.cloud.api.common.CloudInstance
import dev.slne.surf.cloud.api.common.startSpringApplication
import dev.slne.surf.tab.SurfTablistApplication
import dev.slne.surf.tab.velocity.command.surfTabCommand
import dev.slne.surf.tab.velocity.listener.ConnectionListener
import java.nio.file.Path

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    @param:DataDirectory val dataPath: Path,
    suspendingPluginContainer: SuspendingPluginContainer,
    val pluginContainer: PluginContainer
) {
    init {
        suspendingPluginContainer.initialize(this)
        CloudInstance.startSpringApplication(SurfTablistApplication::class)
    }

    @Subscribe
    fun onInitialization(event: ProxyInitializeEvent) {
        instance = this

        surfTabCommand()

        plugin.proxy.eventManager.register(plugin, ConnectionListener())
    }

    companion object {
        lateinit var instance: VelocityMain
    }
}

val plugin get() = VelocityMain.instance
val container = VelocityMain.instance.pluginContainer