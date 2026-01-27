package dev.slne.surf.tab.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.clan.api.clan.Clan
import dev.slne.clan.api.clan.ClanView
import dev.slne.clan.api.clan.listener.ClanCreatedListener
import dev.slne.clan.api.clan.listener.ClanDeletedListener
import dev.slne.clan.api.clan.listener.ClanUpdateMemberListener
import dev.slne.clan.api.clan.listener.ClanUpdatedListener
import dev.slne.surf.redis.RedisApi
import dev.slne.surf.redis.sync.set.SyncSet
import dev.slne.surf.redis.sync.set.SyncSetChange
import dev.slne.surf.tab.api.redis.TabEntryUpdateRedisEvent
import dev.slne.surf.tab.velocity.command.surfTabCommand
import dev.slne.surf.tab.velocity.config.TablistConfigProvider
import dev.slne.surf.tab.velocity.hook.LuckPermsHook
import dev.slne.surf.tab.velocity.listener.ConnectionListener
import dev.slne.surf.tab.velocity.redis.TabRedisEventListener
import dev.slne.surf.tab.velocity.service.tablistService
import org.slf4j.Logger
import java.nio.file.Path
import java.util.*
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

    lateinit var afkPlayers: SyncSet<UUID>

    @Subscribe
    fun onInitialization(event: ProxyInitializeEvent) {
        instance = this
        redisApi = RedisApi.create()

        surfTabCommand()
        LuckPermsHook.load()
        startTask()

        afkPlayers = redisApi.createSyncSet("surf-playtime:afk-players")

        redisApi.subscribeToEvents(TabRedisEventListener)

        plugin.proxy.eventManager.register(plugin, ConnectionListener())
        redisApi.freezeAndConnect()

        Clan.registerListener(object : ClanCreatedListener {
            override fun onClanCreated(clan: Clan) {
                clan.members.map { member -> member.uuid }.forEach { memberUuid ->
                    redisApi.publishEvent(TabEntryUpdateRedisEvent(memberUuid))
                }
            }
        })

        Clan.registerListener(object : ClanUpdatedListener {
            override fun onClanUpdated(clan: Clan) {
                clan.members.map { member -> member.uuid }.forEach { memberUuid ->
                    redisApi.publishEvent(TabEntryUpdateRedisEvent(memberUuid))
                }
            }
        })

        Clan.registerListener(object : ClanUpdateMemberListener {
            override fun onClanMemberUpdated(clan: Clan, memberUuid: UUID, added: Boolean) {
                clan.members.map { member -> member.uuid }.forEach { memberUuid ->
                    redisApi.publishEvent(TabEntryUpdateRedisEvent(memberUuid))
                }
            }
        })

        Clan.registerListener(object : ClanDeletedListener {
            override fun onClanDeleted(clan: ClanView) {
                clan.members.map { member -> member.uuid }.forEach { memberUuid ->
                    redisApi.publishEvent(TabEntryUpdateRedisEvent(memberUuid))
                }
            }
        })

        afkPlayers.addListener {
            when (it) {
                is SyncSetChange.Added<*> -> {
                    redisApi.publishEvent(TabEntryUpdateRedisEvent(it.element as UUID))
                }

                is SyncSetChange.Removed<*> -> {
                    redisApi.publishEvent(TabEntryUpdateRedisEvent(it.element as UUID))
                }

                else -> Unit
            }
        }
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