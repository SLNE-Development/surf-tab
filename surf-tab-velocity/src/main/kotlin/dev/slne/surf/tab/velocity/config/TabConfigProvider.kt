package dev.slne.surf.tab.velocity.config

import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.tab.api.TabDisplayMode
import dev.slne.surf.tab.velocity.plugin

class TabConfigProvider {
    private val configManager: SpongeConfigManager<TabConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(TabConfig::class.java, plugin.dataPath, "config.yml")
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(TabConfig::class.java)

        this.reload()
    }

    fun reload() {
        configManager.reloadFromFile()

        if (config().displayMode == TabDisplayMode.CLOUD_GLOBAL || config().displayMode == TabDisplayMode.PER_WORLD) {
            logger().atWarning()
                .log("TabDisplayMode ${config().displayMode} is not supported on Velocity, switching to PER_PROXY")
            configManager.config = configManager.config.copy(displayMode = TabDisplayMode.PER_PROXY)
        }
    }

    fun config() = configManager.config
}