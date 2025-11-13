package dev.slne.surf.tab.bukkit

import dev.slne.surf.cloud.api.common.CloudInstance
import dev.slne.surf.cloud.api.common.startSpringApplication
import dev.slne.surf.tab.SurfTablistApplication
import dev.slne.surf.tab.core.common.ContextHolderImpl
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap

@Suppress("UnstableApiUsage")
class BukkitBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        ContextHolderImpl.instance.context =
            CloudInstance.startSpringApplication(SurfTablistApplication::class)
    }
}