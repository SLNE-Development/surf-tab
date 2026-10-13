package dev.slne.surf.tab.minestom.listener

import dev.slne.surf.api.minestom.event.EventRegistrar
import dev.slne.surf.api.minestom.extension.addListener
import dev.slne.surf.tab.core.client.service.TablistService
import dev.slne.surf.tab.minestom.platform.MinestomTabPlayer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent

object PlayerListener : EventRegistrar {
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
