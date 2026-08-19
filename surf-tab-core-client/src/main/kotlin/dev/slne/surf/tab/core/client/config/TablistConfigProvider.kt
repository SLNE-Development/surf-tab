package dev.slne.surf.tab.core.client.config

import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.surfConfigApi
import dev.slne.surf.tab.core.client.platform.TabPlatform

class TablistConfigProvider {
    private val configManager: SpongeConfigManager<TablistConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            TablistConfig::class.java,
            TabPlatform.dataPath,
            "config.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(TablistConfig::class.java)

        this.reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}
