package dev.slne.surf.tab.paper.hook

import dev.slne.surf.tab.paper.plugin
import dev.slne.surf.tab.paper.service.tablistService
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.event.user.UserDataRecalculateEvent
import net.luckperms.api.model.user.User
import org.bukkit.Bukkit
import java.util.*

object LuckPermsHook {
    private val luckPerms by lazy {
        LuckPermsProvider.get()
    }

    fun getPrefix(player: UUID) =
        getUser(player)?.primaryGroup?.let {
            luckPerms.groupManager.getGroup(it)?.cachedData?.metaData?.prefix ?: ""
        } ?: ""

    fun getSuffix(player: UUID) =
        getUser(player)?.primaryGroup?.let {
            luckPerms.groupManager.getGroup(it)?.cachedData?.metaData?.suffix ?: ""
        } ?: ""

    fun getWeight(player: UUID) =
        getUser(player)?.primaryGroup?.let {
            luckPerms.groupManager.getGroup(it)?.weight?.orElse(0) ?: 0
        } ?: 0

    fun load() {
        luckPerms.eventBus.subscribe(plugin, NodeAddEvent::class.java) { event ->
            val user = event.target as? User ?: return@subscribe
            updatePlayerInTablist(user)
        }

        luckPerms.eventBus.subscribe(plugin, NodeRemoveEvent::class.java) { event ->
            val user = event.target as? User ?: return@subscribe
            updatePlayerInTablist(user)
        }

        luckPerms.eventBus.subscribe(plugin, UserDataRecalculateEvent::class.java) { event ->
            updatePlayerInTablist(event.user)
        }
    }

    private fun updatePlayerInTablist(user: User) {
        Bukkit.getPlayer(user.uniqueId)?.let {
            tablistService.formatPlayer(it)
        }
    }

    private fun getUser(uuid: UUID) = luckPerms.userManager.getUser(uuid)
}