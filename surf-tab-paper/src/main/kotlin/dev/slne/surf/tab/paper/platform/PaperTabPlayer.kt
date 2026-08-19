package dev.slne.surf.tab.paper.platform

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.tab.core.client.platform.TabPlayer
import dev.slne.surf.tab.paper.plugin
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class PaperTabPlayer(private val player: Player) : TabPlayer {
    override val uuid get() = player.uniqueId
    override val audience get() = player

    override fun baseName(): Component = player.displayName()

    override suspend fun showTabName(name: Component) {
        plugin.launch(plugin.entityDispatcher(player)) {
            player.playerListName(name)
        }
    }

    override suspend fun showTabOrder(order: Int) {
        plugin.launch(plugin.entityDispatcher(player)) {
            player.playerListOrder = order
        }
    }
}
