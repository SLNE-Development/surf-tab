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
import dev.slne.surf.tab.core.common.ContextHolderImpl
import dev.slne.surf.tab.core.common.SyncValues
import dev.slne.surf.tab.velocity.command.surfTabCommand
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
        ContextHolderImpl.instance.context =
            CloudInstance.startSpringApplication(SurfTablistApplication::class)

        SyncValues.init()
    }

    @Subscribe
    fun onInitialization(event: ProxyInitializeEvent) {
        instance = this

        surfTabCommand()

        plugin.proxy.eventManager.register(plugin, ConnectionListener())
        plugin.logger.info("LOGGER WORKING:::::::::::::::::::::::::::::::::::::")
    }

    companion object {
        lateinit var instance: VelocityMain
    }
}

val plugin get() = VelocityMain.instance