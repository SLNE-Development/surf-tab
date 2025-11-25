package dev.slne.surf.tab.velocity.service

import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.velocity.util.currentPlatform
import dev.slne.surf.tab.velocity.util.toVelocity
import net.kyori.adventure.text.Component
import org.springframework.stereotype.Service
import java.util.*

@Service
class VelocityTablistService {
    fun addPlayer(viewer: CloudPlayer, entry: TabEntry) {
        val velocityPlayer = viewer.currentPlatform
        velocityPlayer.tabList.addEntry(entry.toVelocity(velocityPlayer.tabList))
    }

    fun removePlayer(viewer: CloudPlayer, entryUuid: UUID) {
        val velocityPlayer = viewer.currentPlatform
        velocityPlayer.tabList.removeEntry(entryUuid)
    }

    fun sendAdditions(player: CloudPlayer, header: Component, footer: Component) {
        val velocityPlayer = player.currentPlatform

        velocityPlayer.sendPlayerListHeaderAndFooter(
            header,
            footer
        )
    }
}