package dev.slne.surf.tab.velocity.hook

import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.service.tablistService
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.model.user.User
import java.util.*
import kotlin.jvm.optionals.getOrNull

object LuckPermsHook {
    private val luckPerms by lazy {
        LuckPermsProvider.get()
    }

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
        val player = plugin.proxy.getPlayer(user.uniqueId).getOrNull() ?: return
        tablistService.updatePlayerInTablist(player)
    }


}