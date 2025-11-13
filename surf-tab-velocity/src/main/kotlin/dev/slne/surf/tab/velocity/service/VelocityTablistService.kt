package dev.slne.surf.tab.velocity.service

import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.velocity.util.currentPlatform
import dev.slne.surf.tab.velocity.util.formatWithAdventure
import dev.slne.surf.tab.velocity.util.toVelocity
import org.springframework.stereotype.Component

@Component
class VelocityTablistService {
    fun addPlayer(viewer: CloudPlayer, entry: TabEntry) {
        val velocityPlayer = viewer.currentPlatform
        velocityPlayer.tabList.addEntry(entry.toVelocity(velocityPlayer.tabList))
    }
    
    fun removePlayer(viewer: CloudPlayer, entryName: String) {
        val velocityPlayer = viewer.currentPlatform
        velocityPlayer.tabList.entries.firstOrNull { it.profile.name == entryName }?.let {
            velocityPlayer.tabList.removeEntry(it.profile.id)
        }
    }

    fun sendAdditions(player: CloudPlayer, header: String, footer: String) {
        val velocityPlayer = player.currentPlatform

        velocityPlayer.sendPlayerListHeaderAndFooter(
            header.formatWithAdventure(player),
            footer.formatWithAdventure(player)
        )
    }
}