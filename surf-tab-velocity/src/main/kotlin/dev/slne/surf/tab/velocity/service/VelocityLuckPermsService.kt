package dev.slne.surf.tab.velocity.service

import com.google.auto.service.AutoService
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.core.service.LuckPermsService
import dev.slne.surf.tab.core.service.tabService
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.util.tabPlayer
import net.kyori.adventure.util.Services
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.event.user.UserDataRecalculateEvent

@AutoService(LuckPermsService::class)
class VelocityLuckPermsService : LuckPermsService, Services.Fallback {
    override fun getWeight(tabPlayer: TabPlayer): Int {
        val luckperms = LuckPermsProvider.get()
        val group = luckperms.userManager.getUser(tabPlayer.uniqueId)?.primaryGroup ?: "default"
        return luckperms.groupManager.getGroup(group)?.weight?.orElse(0) ?: 0
    }

    override fun registerListener() {
        val luckperms = LuckPermsProvider.get()
        luckperms.eventBus.subscribe(plugin, UserDataRecalculateEvent::class.java, ::onNodeMutate)
    }

    fun onNodeMutate(event: UserDataRecalculateEvent) {
        plugin.proxy.allPlayers.forEach {
            tabService.sendTablistUpdate(it.tabPlayer())
        }
    }
}