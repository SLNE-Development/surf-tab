package dev.slne.surf.tab.minestom.hook

import dev.slne.surf.api.core.luckperms.LuckPermsAccess
import dev.slne.surf.tab.core.client.hook.LuckPermsHook
import net.luckperms.api.event.node.NodeAddEvent
import net.luckperms.api.event.node.NodeRemoveEvent
import net.luckperms.api.event.user.UserDataRecalculateEvent
import net.luckperms.api.model.user.User

fun registerLuckPermsListeners() {
    val eventBus = LuckPermsAccess.luckperms.eventBus

    eventBus.subscribe(NodeAddEvent::class.java) { event ->
        val user = event.target as? User ?: return@subscribe
        LuckPermsHook.updatePlayerInTablist(user)
    }

    eventBus.subscribe(NodeRemoveEvent::class.java) { event ->
        val user = event.target as? User ?: return@subscribe
        LuckPermsHook.updatePlayerInTablist(user)
    }

    eventBus.subscribe(UserDataRecalculateEvent::class.java) { event ->
        LuckPermsHook.updatePlayerInTablist(event.user)
    }
}
