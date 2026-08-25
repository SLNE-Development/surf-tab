package dev.slne.surf.tab.paper.platform

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import dev.slne.surf.tab.core.client.platform.TabPlayer
import dev.slne.surf.tab.paper.plugin
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class PaperTabPlayer(private val player: Player) : TabPlayer {
    override val uuid get() = player.uniqueId
    override val audience get() = player

    override fun baseName(): Component = player.displayName()

    override suspend fun showTabEntry(name: Component, order: Int) {
        withContext(plugin.entityDispatcher(player)) {
            player.playerListName(name)
            player.playerListOrder = order
        }
    }
}
