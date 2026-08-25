package dev.slne.surf.tab.minestom.platform

import dev.slne.minestom.lobby.api.coroutine.withEntity
import dev.slne.surf.api.core.luckperms.LuckPermsAccess
import dev.slne.surf.api.core.luckperms.prefix
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.tab.core.client.platform.TabPlayer
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import java.util.*

class MinestomTabPlayer(private val player: Player) : TabPlayer {
    override val uuid: UUID get() = player.uuid
    override val audience get() = player

    override fun baseName(): Component =
        miniMessage.deserialize("${LuckPermsAccess.getUser(player.uuid)?.prefix ?: ""}${player.username}")

    override suspend fun baseNameSnapshot(): Component = player.withEntity { baseName() }

    override suspend fun showTabEntry(name: Component, order: Int) {
        player.withEntity {
            it.displayName = name
            it.listOrder = order
        }
    }
}
