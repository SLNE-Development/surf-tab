package dev.slne.surf.tab.velocity.service

import dev.slne.surf.tab.api.model.TabEntry
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.core.service.TabService
import dev.slne.surf.tab.velocity.tabConfig
import dev.slne.surf.tab.velocity.util.formatMiniMessage
import dev.slne.surf.tab.velocity.util.velocityPlayer
import net.kyori.adventure.util.Services
import java.util.*

class VelocityTablistService : TabService, Services.Fallback {
    override fun sendTablistUpdate(player: TabPlayer) {
        sendHeader(player)
        sendFooter(player)
    }

    override fun sendHeader(player: TabPlayer) {
        val velocityPlayer = player.velocityPlayer() ?: return

        tabConfig.config.header.formatMiniMessage(player.uniqueId).thenAccept {
            velocityPlayer.sendPlayerListHeader(it)
        }
    }

    override fun sendFooter(player: TabPlayer) {
        val velocityPlayer = player.velocityPlayer() ?: return

        tabConfig.config.footer.formatMiniMessage(player.uniqueId).thenAccept {
            velocityPlayer.sendPlayerListFooter(it)
        }
    }

    override fun showEntry(
        player: TabPlayer,
        entry: TabEntry
    ) {
        TODO("Not yet implemented")
    }

    override fun hideEntry(
        player: TabPlayer,
        entry: TabEntry
    ) {
        TODO("Not yet implemented")
    }

    override fun updateEntry(
        player: TabPlayer,
        associatedWithEntry: UUID
    ) {

    }
}