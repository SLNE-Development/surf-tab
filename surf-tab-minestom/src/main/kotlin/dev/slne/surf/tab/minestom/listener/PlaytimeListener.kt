package dev.slne.surf.tab.minestom.listener

import dev.slne.surf.api.minestom.event.EventRegistrar
import dev.slne.surf.api.minestom.extension.addListener
import dev.slne.surf.playtime.api.minestom.event.AfkStateChangeEvent
import dev.slne.surf.tab.core.client.service.TablistService
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

/**
 * Keeps the afk tag of a player in sync with their afk state.
 */
object PlaytimeListener : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<AfkStateChangeEvent> { event ->
            TablistService.updateEntry(event.player.uuid)
        }
    }
}
