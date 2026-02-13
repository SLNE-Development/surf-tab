package dev.slne.surf.tab.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.tab.paper.command.surfTabCommand
import dev.slne.surf.tab.paper.config.TablistConfigProvider
import dev.slne.surf.tab.paper.hook.LuckPermsHook
import dev.slne.surf.tab.paper.listener.ConnectionListener
import dev.slne.surf.tab.paper.service.tablistService
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {

    override fun onEnable() {
        redisLoader.connect()
        surfTabCommand()

        tablistService.startTask()
        LuckPermsHook.load()
        ConnectionListener.register()
    }

    override fun onDisable() {
        tablistService.cancelTask()
        redisLoader.disconnect()
    }
}

val tablistConfiguration = TablistConfigProvider()
val tablistConfig get() = tablistConfiguration.config