package dev.slne.surf.tab.velocity.service

import com.velocitypowered.api.proxy.Player
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.util.toVelocity
import net.kyori.adventure.text.Component
import java.util.*

val tablistService = VelocityTablistService()

class VelocityTablistService {
    fun addPlayer(viewer: Player, entry: TabEntry) {
        plugin.logger.info("Adding tablist entry ${entry.displayName} to player ${viewer.username}")
        viewer.tabList.addEntry(entry.toVelocity(viewer.tabList))
    }

    fun removePlayer(viewer: Player, entryUuid: UUID) {
        plugin.logger.info("Removing tablist entry $entryUuid from player ${viewer.username}")
        viewer.tabList.removeEntry(entryUuid)
    }

    fun sendAdditions(player: Player, header: Component, footer: Component) {
        player.sendPlayerListHeaderAndFooter(header, footer)
    }
}