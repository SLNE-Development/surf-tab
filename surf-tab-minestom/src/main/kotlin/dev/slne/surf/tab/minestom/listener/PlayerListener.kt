package dev.slne.surf.tab.minestom.listener

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.event.EventRegistrar
import dev.slne.minestom.lobby.api.extension.addListener
import dev.slne.surf.tab.core.client.service.TablistService
import dev.slne.surf.tab.minestom.platform.MinestomTabPlayer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent

class PlayerListener @Inject constructor() : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<PlayerSpawnEvent> { event ->
            if (!event.isFirstSpawn) return@addListener

            TablistService.viewerJoined(MinestomTabPlayer(event.player))
        }

        node.addListener<PlayerDisconnectEvent> { event ->
            TablistService.viewerLeft(event.player.uuid)
        }
    }
}
