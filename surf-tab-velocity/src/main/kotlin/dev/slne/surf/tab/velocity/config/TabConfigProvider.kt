package dev.slne.surf.tab.velocity.config

import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.tab.velocity.plugin

class TabConfigProvider {
    val configManager: SpongeConfigManager<TabConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(TabConfig::class.java, plugin.dataPath, "config.yml")
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(TabConfig::class.java)

        this.reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config by lazy {
        configManager.config
    }
}