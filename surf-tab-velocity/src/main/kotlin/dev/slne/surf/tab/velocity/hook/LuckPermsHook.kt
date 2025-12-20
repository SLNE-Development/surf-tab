package dev.slne.surf.tab.velocity.hook

import com.github.retrooper.packetevents.protocol.player.User
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.service.tablistService
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import java.util.*
import kotlin.jvm.optionals.getOrNull

object LuckPermsHook {
    private val luckPerms by lazy {
        LuckPermsProvider.get()
    }

    fun getPrefix(player: UUID) =
        luckPerms.userManager.getUser(player)?.cachedData?.metaData?.prefix
            ?: ""

    fun getSuffix(player: UUID) =
        luckPerms.userManager.getUser(player)?.cachedData?.metaData?.suffix
            ?: ""

    fun getWeight(player: UUID) =
        luckPerms.userManager.getUser(player)?.cachedData?.metaData?.getMetaValue("weight")
            ?.toInt() ?: 0

    fun load() {
        luckPerms.eventBus.subscribe(plugin, NodeAddEvent::class.java) { event ->
            val user = event.target as? User ?: return@subscribe
            updatePlayerInTablist(user)
        }

        luckPerms.eventBus.subscribe(plugin, NodeRemoveEvent::class.java) { event ->
            val user = event.target as? User ?: return@subscribe
            updatePlayerInTablist(user)
        }
    }

    private fun updatePlayerInTablist(user: User) {
        val player = plugin.proxy.getPlayer(user.uuid).getOrNull() ?: return
        val server = player.currentServer.getOrNull()?.server ?: return

        val seenServers = tablistService.getSeenServers(server)
        val viewers = seenServers.flatMap { it.playersConnected }.distinct()

        viewers.forEach { viewer ->
            tablistService.removePlayer(viewer, player.uniqueId)
            tablistService.addPlayer(viewer, tablistService.createEntry(player, viewer))
        }

        tablistService.removePlayer(player, player.uniqueId)
        tablistService.addPlayer(player, tablistService.createEntry(player, player))
    }

}