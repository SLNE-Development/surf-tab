package dev.slne.surf.tab.velocity.service

import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.velocity.util.currentPlatform
import dev.slne.surf.tab.velocity.util.formatWithAdventure
import dev.slne.surf.tab.velocity.util.toVelocity
import it.unimi.dsi.fastutil.objects.ObjectList
import org.springframework.stereotype.Component

@Component
class VelocityTablistService(
    private val syncService: VelocitySyncService
) {
    fun sendTablistUpdate(player: CloudPlayer, entries: ObjectList<TabEntry>) {
        this.sendEssentials(player)

        this.clearActualTablist(player)
        this.sendTablist(player, entries)
    }

    fun addPlayer()

    private fun sendEssentials(player: CloudPlayer) {
        val velocityPlayer = player.currentPlatform

        velocityPlayer.sendPlayerListHeaderAndFooter(
            syncService.tabHeader.get().formatWithAdventure(player),
            syncService.tabFooter.get().formatWithAdventure(player)
        )
    }

    private fun clearActualTablist(player: CloudPlayer) {
        val velocityPlayer = player.currentPlatform
        velocityPlayer.tabList.entries.forEach { velocityPlayer.tabList.removeEntry(it.profile.id) }
    }

    private fun sendTablist(player: CloudPlayer, entries: ObjectList<TabEntry>) {
        val velocityPlayer = player.currentPlatform

        velocityPlayer.tabList.addEntries(entries.map {
            it.toVelocity(velocityPlayer.tabList)
        })
    }
}