package dev.slne.surf.tab.minestom

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.plugin.MinestomPlugin
import dev.slne.minestom.lobby.api.plugin.annotation.MinestomPluginMeta
import dev.slne.surf.tab.minestom.command.TabCommandRegistrar
import dev.slne.surf.tab.minestom.listener.PlayerListener
import dev.slne.surf.tab.minestom.listener.PlaytimeListener

@AutoService(MinestomPlugin::class)
@MinestomPluginMeta(
    "surf-tab-minestom",
    dependsOn = [
        "surf-api-minestom",
        "surf-redis-minestom",
        "surf-core-minestom",
        "surf-clan-minestom",
        "surf-playtime-minestom",
        "surf-content-creator-minestom"
    ]
)
class TabMinestomPlugin : MinestomPlugin(TabMinestomEntrypoint::class.java) {
    override fun configurePlugin() {
        bindCommandRegistrar<TabCommandRegistrar>()
        bindEventRegistrar<PlayerListener>()
        bindEventRegistrar<PlaytimeListener>()
    }
}
