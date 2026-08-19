package dev.slne.surf.tab.minestom.platform

import dev.slne.minestom.lobby.api.coroutine.withEntity
import dev.slne.surf.tab.core.client.platform.TabPlayer
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import java.util.*

class MinestomTabPlayer(private val player: Player) : TabPlayer {
    override val uuid: UUID get() = player.uuid
    override val audience get() = player

    override fun baseName(): Component = Component.text(player.username)

    override suspend fun showTabName(name: Component) {
        player.withEntity {
            it.displayName = name
        }
    }

    override suspend fun showTabOrder(order: Int) {
        player.withEntity {
            it.listOrder = order
        }
    }
}
