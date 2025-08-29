package dev.slne.surf.tab.velocity.service

import com.google.auto.service.AutoService
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.player.TabListEntry
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import dev.slne.surf.tab.api.TabDisplayMode
import dev.slne.surf.tab.api.TabEntry
import dev.slne.surf.tab.api.player.TabGameMode
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.core.model.TabEntryImpl
import dev.slne.surf.tab.core.service.TabService
import dev.slne.surf.tab.core.service.luckPermsService
import dev.slne.surf.tab.core.service.tabGroupService
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.tabConfig
import dev.slne.surf.tab.velocity.util.*
import net.kyori.adventure.util.Services
import java.util.*
import kotlin.jvm.optionals.getOrNull

@AutoService(TabService::class)
class VelocityTablistService : TabService, Services.Fallback {
    override fun sendTablistUpdate(player: TabPlayer) {
        sendHeader(player)
        sendFooter(player)

        clearActualTablist(player)
        sendFakeTablist(player)
    }

    override fun clearActualTablist(player: TabPlayer) {
        val velocityPlayer = player.velocityPlayer() ?: return
        velocityPlayer.tabList.entries.forEach { velocityPlayer.tabList.removeEntry(it.profile.id) }
    }

    override fun sendFakeTablist(player: TabPlayer) {
        val velocityPlayer = player.velocityPlayer() ?: return

        when (tabMode) {
            TabDisplayMode.PER_PLAYER -> sendPlayerTablist(player, velocityPlayer)
            TabDisplayMode.PER_SERVER -> sendServerTablist(player, velocityPlayer)
            TabDisplayMode.PER_PROXY -> sendProxyTablist(player, velocityPlayer)
            TabDisplayMode.PER_PROXY_WITH_GROUPS -> sendPerProxyWithGroupsTablist(
                player,
                velocityPlayer
            )

            TabDisplayMode.CLOUD_GLOBAL, TabDisplayMode.PER_WORLD, TabDisplayMode.CLOUD_GLOBAL_WITH_GROUPS -> logger().atWarning()
                .log("TabDisplayMode $tabMode are not supported on Velocity!")
        }
    }

    fun sendPerProxyWithGroupsTablist(player: TabPlayer, velocityPlayer: Player) {
        val group = tabGroupService.getGroupForPlayer(player.uniqueId) ?: run {
            sendServerTablist(player, velocityPlayer)
            return
        }

        showEntries(player, group.retrievePlayers().mapNotNull {
            val velocityGroupPlayer = it.velocityPlayer() ?: return@mapNotNull null
            val profile = velocityGroupPlayer.gameProfile.toTabProfile()
            val display =
                display.formatWithAdventure(velocityGroupPlayer, velocityPlayer)
            val gameMode = TabGameMode.CREATIVE
            val ping = velocityGroupPlayer.ping.toInt()
            val weight = luckPermsService.getWeight(it)

            TabEntryImpl(
                profile,
                display,
                gameMode,
                ping,
                weight
            )
        }.toObjectSet())
    }

    fun sendPlayerTablist(player: TabPlayer, velocityPlayer: Player) {
        showEntry(
            player, TabEntryImpl(
                velocityPlayer.gameProfile.toTabProfile(),
                display.formatWithAdventure(
                    velocityPlayer,
                    velocityPlayer
                ),
                TabGameMode.CREATIVE,
                velocityPlayer.ping.toInt(),
                luckPermsService.getWeight(velocityPlayer.tabPlayer())
            )
        )
    }

    fun sendServerTablist(player: TabPlayer, velocityPlayer: Player) {
        val server = velocityPlayer.currentServer.getOrNull()?.server ?: return

        showEntries(player, server.playersConnected.map {
            val profile = it.gameProfile.toTabProfile()
            val display =
                display.formatWithAdventure(it, velocityPlayer)
            val gameMode = TabGameMode.CREATIVE
            val ping = it.ping.toInt()
            val weight = luckPermsService.getWeight(it.tabPlayer())

            TabEntryImpl(
                profile,
                display,
                gameMode,
                ping,
                weight
            )
        }.toObjectSet())
    }

    fun sendProxyTablist(player: TabPlayer, velocityPlayer: Player) {
        showEntries(player, plugin.proxy.allPlayers.map {
            val profile = it.gameProfile.toTabProfile()
            val display =
                display.formatWithAdventure(it, velocityPlayer)
            val gameMode = TabGameMode.CREATIVE
            val ping = it.ping.toInt()
            val weight = luckPermsService.getWeight(it.tabPlayer())

            TabEntryImpl(
                profile,
                display,
                gameMode,
                ping,
                weight
            )
        }.toObjectSet())
    }

    override fun sendHeader(player: TabPlayer) {
        val velocityPlayer = player.velocityPlayer() ?: return
        val header = header.formatWithAdventure(velocityPlayer)

        velocityPlayer.sendPlayerListHeader(header)
    }

    override fun sendFooter(player: TabPlayer) {
        val velocityPlayer = player.velocityPlayer() ?: return
        val footer = footer.formatWithAdventure(velocityPlayer)

        velocityPlayer.sendPlayerListFooter(footer)
    }

    override fun showEntry(
        player: TabPlayer,
        entry: TabEntry
    ) {
        val velocityPlayer = player.velocityPlayer() ?: return
        val targetPlayer = plugin.proxy.getPlayer(entry.profile.uuid).getOrNull() ?: return

        velocityPlayer.tabList.addEntry(
            TabListEntry.builder()
                .tabList(targetPlayer.tabList)
                .profile(targetPlayer.gameProfile)
                .displayName(entry.display)
                .latency(targetPlayer.ping.toInt())
                .gameMode(TabGameMode.CREATIVE.index)
                .listed(true)
                .listOrder(entry.weight)
                .showHat(true)
                .build()
        )
    }

    override fun hideEntry(
        player: TabPlayer,
        entry: UUID
    ) {
        val velocityPlayer = player.velocityPlayer() ?: return
        velocityPlayer.tabList.removeEntry(entry)
    }

    override fun updateEntry(
        player: TabPlayer,
        associatedWithEntry: UUID,
        block: (TabEntry) -> Unit
    ) {
        val entry =
            player.velocityPlayer()?.tabList?.entries?.find { it.profile.id == associatedWithEntry }
                ?.toTabEntry()
                ?: return
        hideEntry(player, associatedWithEntry)
        showEntry(player, entry.apply(block))
    }

    val header get() = tabConfig.config().header
    val footer get() = tabConfig.config().footer
    val display get() = tabConfig.config().displayName
    val tabMode get() = tabConfig.config().displayMode
}