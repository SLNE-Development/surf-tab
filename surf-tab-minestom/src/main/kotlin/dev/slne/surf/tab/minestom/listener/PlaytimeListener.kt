package dev.slne.surf.tab.minestom.listener

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.event.EventRegistrar
import dev.slne.minestom.lobby.api.extension.addListener
import dev.slne.surf.playtime.api.minestom.event.AfkStateChangeEvent
import dev.slne.surf.tab.core.client.service.TablistService
import dev.slne.surf.tab.minestom.platform.MinestomTabPlayer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

/**
 * Keeps the afk tag of a player in sync with their afk state.
 */
class PlaytimeListener @Inject constructor() : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<AfkStateChangeEvent> { event ->
            TablistService.requestFormat(MinestomTabPlayer(event.player))
        }
    }
}
