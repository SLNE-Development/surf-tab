package dev.slne.surf.tab.velocity.service

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.server.RegisteredServer
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.api.entry.TabGameMode
import dev.slne.surf.tab.velocity.hook.LuckPermsHook
import dev.slne.surf.tab.velocity.tablistConfig
import dev.slne.surf.tab.velocity.util.formatWithAdventure
import dev.slne.surf.tab.velocity.util.getServers
import dev.slne.surf.tab.velocity.util.toTabProfile
import dev.slne.surf.tab.velocity.util.toVelocity
import net.kyori.adventure.text.Component
import java.util.*

val tablistService = VelocityTablistService()

class VelocityTablistService {
    fun addPlayer(viewer: Player, entry: TabEntry) {
        viewer.tabList.addEntry(entry.toVelocity(viewer.tabList))
    }

    fun removePlayer(viewer: Player, entryUuid: UUID) {
        viewer.tabList.removeEntry(entryUuid)
    }

    fun sendAdditions(player: Player, header: Component, footer: Component) {
        player.sendPlayerListHeaderAndFooter(header, footer)
    }

    fun getSeenServers(base: RegisteredServer): List<RegisteredServer> {
        val groups = tablistConfig.groups.map { it.toTabGroup() }

        return groups
            .filter { base in it.getServers() }
            .flatMap { it.getServers() }
            .distinct()
    }

    fun createEntry(target: Player, viewer: Player) = TabEntry(
        profile = target.gameProfile.toTabProfile(),
        displayName = tablistConfig.nameFormat.formatWithAdventure(target, viewer),
        ping = target.ping.toInt(),
        gameMode = TabGameMode.SURVIVAL,
        weight = LuckPermsHook.getWeight(target.uniqueId)
    )
}