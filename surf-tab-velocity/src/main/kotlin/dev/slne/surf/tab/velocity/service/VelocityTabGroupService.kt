package dev.slne.surf.tab.velocity.service

import com.google.auto.service.AutoService
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.api.server.TabGroup
import dev.slne.surf.tab.core.model.TabGroupImpl
import dev.slne.surf.tab.core.model.TabServerImpl
import dev.slne.surf.tab.core.service.TabGroupService
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.tabGroupConfig
import dev.slne.surf.tab.velocity.util.tabPlayer
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*
import kotlin.jvm.optionals.getOrNull

@AutoService(TabGroupService::class)
class VelocityTabGroupService : TabGroupService, Services.Fallback {
    private val groups: ObjectSet<TabGroup> = mutableObjectSetOf()
    override fun loadGroups(notify: Boolean) {
        groups.clear()
        groups.addAll(tabGroupConfig.config.groups.map {
            TabGroupImpl(
                it.name,
                it.velocityServers.map { svr ->
                    TabServerImpl(
                        svr
                    )
                }.toObjectSet()
            )
        })

        if (notify && groups.isNotEmpty()) {
            logger().atInfo().log("Loaded ${groups.size} tab groups from config.")
        }
    }

    override fun getGroup(groupName: String) = groups.find { it.name == groupName }
    override fun getGroups() = groups
    override fun getGroupForServer(serverName: String) =
        groups.find { it.servers.any { srv -> srv.name == serverName } }

    override fun getGroupForPlayer(uuid: UUID): TabGroup? =
        plugin.proxy.allServers.find { registeredServer ->
            registeredServer.playersConnected.any { it.uniqueId == uuid }
        }?.serverInfo?.name?.let {
            getGroupForServer(it)
        }

    override fun retrievePlayers(group: TabGroup): ObjectSet<TabPlayer> = plugin.proxy.allPlayers
        .filter { player ->
            group.servers.any { srv -> srv.name == player.currentServer.getOrNull()?.serverInfo?.name }
        }.map { it.tabPlayer() }.toObjectSet()
}