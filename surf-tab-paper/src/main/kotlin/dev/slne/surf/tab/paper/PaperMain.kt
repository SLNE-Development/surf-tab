package dev.slne.surf.tab.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.api.paper.event.register
import dev.slne.surf.api.paper.extensions.pluginManager
import dev.slne.surf.tab.core.client.hook.ClanHook
import dev.slne.surf.tab.core.client.hook.ContentCreatorHook
import dev.slne.surf.tab.core.client.redis.redisLoader
import dev.slne.surf.tab.paper.command.surfTabCommand
import dev.slne.surf.tab.paper.hook.registerLuckPermsListeners
import dev.slne.surf.tab.paper.listener.PlayerListener
import dev.slne.surf.tab.paper.listener.PlaytimeListener
import dev.slne.surf.tab.paper.listener.VanishListener
import dev.slne.surf.tab.paper.service.tablistTask
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {

    override fun onEnable() {
        redisLoader.connect()
        surfTabCommand()

        tablistTask.startTask()
        registerLuckPermsListeners()
        PlayerListener.register()

        if (isPlaytimeHook) {
            PlaytimeListener.register()
        }

        if (isClansHook) {
            ClanHook.createListeners()
        }

        if (isContentCreatorHook) {
            ContentCreatorHook.registerListener()
        }
        if (isVanishHook) {
            VanishListener.register()
        }
    }

    override fun onDisable() {
        tablistTask.cancelTask()
        redisLoader.disconnect()
    }
}

val isVanishHook get() = pluginManager.isPluginEnabled("surf-vanish-paper")
val isPlaytimeHook get() = pluginManager.isPluginEnabled("surf-playtime-paper")
val isClansHook get() = pluginManager.isPluginEnabled("surf-clan-paper")
val isContentCreatorHook get() = pluginManager.isPluginEnabled("surf-content-creator-paper")
