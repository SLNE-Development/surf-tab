package dev.slne.surf.tab.velocity.service

import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.velocity.util.currentPlatform
import dev.slne.surf.tab.velocity.util.formatWithAdventure
import dev.slne.surf.tab.velocity.util.toVelocity
import org.springframework.stereotype.Component
import java.util.*

@Component
class VelocityTablistService {
    fun addPlayer(viewer: CloudPlayer, entry: TabEntry) {
        val velocityPlayer = viewer.currentPlatform
        velocityPlayer.tabList.addEntry(entry.toVelocity(velocityPlayer.tabList))
    }

    fun removePlayer(viewer: CloudPlayer, entryUuid: UUID) {
        val velocityPlayer = viewer.currentPlatform
        velocityPlayer.tabList.removeEntry(entryUuid)
    }

    fun sendAdditions(player: CloudPlayer, header: String, footer: String) {
        val velocityPlayer = player.currentPlatform

        velocityPlayer.sendPlayerListHeaderAndFooter(
            header.formatWithAdventure(player),
            footer.formatWithAdventure(player)
        )
    }
}