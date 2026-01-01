package dev.slne.surf.tab.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.clan.api.ClanModificationListener
import dev.slne.clan.api.surfClanApi
import dev.slne.surf.redis.RedisApi
import dev.slne.surf.tab.api.redis.TabEntryUpdateRedisEvent
import dev.slne.surf.tab.velocity.command.surfTabCommand
import dev.slne.surf.tab.velocity.config.TablistConfigProvider
import dev.slne.surf.tab.velocity.hook.LuckPermsHook
import dev.slne.surf.tab.velocity.listener.ConnectionListener
import dev.slne.surf.tab.velocity.redis.TabRedisEventListener
import dev.slne.surf.tab.velocity.service.tablistService
import org.slf4j.Logger
import java.nio.file.Path
import java.util.concurrent.TimeUnit

class VelocityMain @Inject constructor(
    val proxy: ProxyServer,
    @param:DataDirectory val dataPath: Path,
    suspendingPluginContainer: SuspendingPluginContainer,
    val pluginContainer: PluginContainer,
    val logger: Logger
) {
    init {
        suspendingPluginContainer.initialize(this)
    }

    @Subscribe
    fun onInitialization(event: ProxyInitializeEvent) {
        instance = this
        redisApi = RedisApi.create(dataPath)

        surfTabCommand()
        LuckPermsHook.load()
        startTask()

        redisApi.subscribeToEvents(TabRedisEventListener)

        plugin.proxy.eventManager.register(plugin, ConnectionListener())
        redisApi.freezeAndConnect()

        surfClanApi.addClanModificationListener(ClanModificationListener {
            it.members.map { member -> member.uuid }.forEach { memberUuid ->
                redisApi.publishEvent(TabEntryUpdateRedisEvent(memberUuid))
            }
        })
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyShutdownEvent) {
        redisApi.disconnect()
    }

    fun startTask() {
        plugin.proxy.scheduler.buildTask(this, Runnable {
            plugin.proxy.allPlayers.forEach {
                tablistService.sendAdditions(it)
            }
        }).repeat(1L, TimeUnit.SECONDS).schedule()
    }

    companion object {
        lateinit var instance: VelocityMain
        lateinit var redisApi: RedisApi
    }
}

val tablistConfiguration = TablistConfigProvider()
val tablistConfig get() = tablistConfiguration.config
val plugin get() = VelocityMain.instance
val redisApi get() = VelocityMain.redisApi