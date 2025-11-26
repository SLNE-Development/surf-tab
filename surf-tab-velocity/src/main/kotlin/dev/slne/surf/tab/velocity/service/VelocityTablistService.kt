package dev.slne.surf.tab.velocity.service

import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.util.currentPlatform
import dev.slne.surf.tab.velocity.util.toVelocity
import net.kyori.adventure.text.Component
import org.springframework.stereotype.Service
import java.util.*

@Service
class VelocityTablistService {
    init {
        plugin.tabEntries.subscribe { added, element ->
            val currentServer = CloudServer.current()

            if (element.first != currentServer.name) {
                return@subscribe
            }

            if (added) {
                currentServer.users.forEach {
                    addPlayer(it, element.second)
                }
            } else {
                currentServer.users.forEach {
                    removePlayer(it, element.second.profile.uuid)
                }
            }
        }
    }


    fun addPlayer(viewer: CloudPlayer, entry: TabEntry) {
        val velocityPlayer = viewer.currentPlatform ?: return
        velocityPlayer.tabList.addEntry(entry.toVelocity(velocityPlayer.tabList))
    }

    fun removePlayer(viewer: CloudPlayer, entryUuid: UUID) {
        val velocityPlayer = viewer.currentPlatform ?: return
        velocityPlayer.tabList.removeEntry(entryUuid)
    }

    fun sendAdditions(player: CloudPlayer, header: Component, footer: Component) {
        val velocityPlayer = player.currentPlatform ?: return

        velocityPlayer.sendPlayerListHeaderAndFooter(
            header,
            footer
        )
    }

    fun reloadTablistUsers() {
        val currentServer = CloudServer.current()

        currentServer.users.forEach {
            it.currentPlatform?.tabList?.clearAll()
        }

        plugin.tabEntries.forEach { entry ->
            if (entry.first != currentServer.name) {
                return@forEach
            }

            currentServer.users.forEach {
                addPlayer(it, entry.second)
            }
        }
    }
}