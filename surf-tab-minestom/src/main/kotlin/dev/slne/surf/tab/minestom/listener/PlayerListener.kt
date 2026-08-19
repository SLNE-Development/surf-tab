package dev.slne.surf.tab.minestom.listener

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.coroutine.minestomScope
import dev.slne.minestom.lobby.api.event.EventRegistrar
import dev.slne.minestom.lobby.api.extension.addListener
import dev.slne.surf.tab.core.client.service.tablistService
import dev.slne.surf.tab.minestom.platform.MinestomTabPlayer
import kotlinx.coroutines.launch
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerSpawnEvent

/**
 * Fills the tablist of a player as soon as they arrive.
 */
class PlayerListener @Inject constructor() : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<PlayerSpawnEvent> { event ->
            if (!event.isFirstSpawn) return@addListener

            val player = MinestomTabPlayer(event.player)
            tablistService.sendAdditions(player)

            minestomScope.launch {
                tablistService.formatPlayer(player)
            }
        }
    }
}
