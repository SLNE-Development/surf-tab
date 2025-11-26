package dev.slne.surf.tab.velocity.service

import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.core.common.SyncValues
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.util.currentPlatform
import dev.slne.surf.tab.velocity.util.toVelocity
import net.kyori.adventure.text.Component
import org.springframework.stereotype.Service
import java.util.*

@Service
class VelocityTablistService {
    init {
        SyncValues.tabEntries.subscribe { added, element ->
            val currentServer = CloudServer.current()

            plugin.logger.debug("Tablist entry ${if (added) "added" else "removed"}: ${element.second.displayName} for server ${element.first}")

            if (element.first != currentServer.name) {
                return@subscribe
            }

            plugin.logger.debug("Updating tablist for server ${currentServer.name}")

            if (added) {
                currentServer.users.forEach {
                    plugin.logger.debug("Adding tablist entry to player ${it.name}")
                    addPlayer(it, element.second)
                }
            } else {
                currentServer.users.forEach {
                    plugin.logger.debug("Removing tablist entry from player ${it.name}")
                    removePlayer(it, element.second.profile.uuid)
                }
            }
        }
    }


    fun addPlayer(viewer: CloudPlayer, entry: TabEntry) {
        val velocityPlayer = viewer.currentPlatform ?: return
        plugin.logger.debug("Adding tablist entry ${entry.displayName} to player ${viewer.name}")
        velocityPlayer.tabList.addEntry(entry.toVelocity(velocityPlayer.tabList))
    }

    fun removePlayer(viewer: CloudPlayer, entryUuid: UUID) {
        val velocityPlayer = viewer.currentPlatform ?: return
        plugin.logger.debug("Removing tablist entry $entryUuid from player ${viewer.name}")
        velocityPlayer.tabList.removeEntry(entryUuid)
    }

    fun sendAdditions(player: CloudPlayer, header: Component, footer: Component) {
        val velocityPlayer = player.currentPlatform ?: return

        velocityPlayer.sendPlayerListHeaderAndFooter(
            header,
            footer
        )
    }
}