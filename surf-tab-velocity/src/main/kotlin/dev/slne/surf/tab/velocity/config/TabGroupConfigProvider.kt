package dev.slne.surf.tab.velocity.config

import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.tab.velocity.plugin

class TabGroupConfigProvider {
    private val configManager: SpongeConfigManager<TabGroupsConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            TabGroupsConfig::class.java,
            plugin.dataPath,
            "groups.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(TabGroupsConfig::class.java)

        this.reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}

val tabGroupConfig get() = TabGroupConfigProvider().config