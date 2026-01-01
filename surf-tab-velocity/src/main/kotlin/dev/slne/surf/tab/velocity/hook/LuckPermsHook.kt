package dev.slne.surf.tab.velocity.hook

import dev.slne.surf.tab.api.redis.TabEntryUpdateRedisEvent
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.redisApi
import kotlinx.coroutines.future.await
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.model.user.User
import java.util.*

object LuckPermsHook {
    private val luckPerms by lazy {
        LuckPermsProvider.get()
    }

    suspend fun getPrefix(player: UUID) =
        getOrLoadUser(player).primaryGroup.let {
            luckPerms.groupManager.getGroup(it)?.cachedData?.metaData?.prefix ?: ""
        }

    suspend fun getSuffix(player: UUID) =
        getOrLoadUser(player).primaryGroup.let {
            luckPerms.groupManager.getGroup(it)?.cachedData?.metaData?.suffix ?: ""
        }

    suspend fun getWeight(player: UUID) =
        getOrLoadUser(player).primaryGroup.let {
            luckPerms.groupManager.getGroup(it)?.weight?.orElse(0) ?: 0
        }

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

    suspend fun getOrLoadUser(player: UUID): User {
        return luckPerms.userManager.getUser(player) ?: luckPerms.userManager.loadUser(player)
            .await()
    }

    private fun updatePlayerInTablist(user: User) {
        redisApi.publishEvent(TabEntryUpdateRedisEvent(user.uniqueId))
    }
}