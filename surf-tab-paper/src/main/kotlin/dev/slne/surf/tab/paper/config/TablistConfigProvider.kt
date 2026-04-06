package dev.slne.surf.tab.paper.config

import dev.slne.surf.api.core.config.SurfConfigApi
import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.tab.paper.plugin

class TablistConfigProvider {
    private val configManager: SpongeConfigManager<TablistConfig>

    init {
        SurfConfigApi.createSpongeYmlConfig(
            TablistConfig::class.java,
            plugin.dataPath,
            "config.yml"
        )
        configManager = SurfConfigApi.getSpongeConfigManagerForConfig(TablistConfig::class.java)

        this.reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}