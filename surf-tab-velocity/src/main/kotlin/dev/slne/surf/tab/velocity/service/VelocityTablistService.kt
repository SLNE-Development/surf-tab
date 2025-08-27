package dev.slne.surf.tab.velocity.service

import com.google.auto.service.AutoService
import com.velocitypowered.api.proxy.player.TabListEntry
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.tab.api.TabDisplayMode
import dev.slne.surf.tab.api.TabEntry
import dev.slne.surf.tab.api.player.TabGameMode
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.core.model.TabEntryImpl
import dev.slne.surf.tab.core.service.TabService
import dev.slne.surf.tab.core.service.luckPermsService
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.tabConfig
import dev.slne.surf.tab.velocity.util.formatWithAdventure
import dev.slne.surf.tab.velocity.util.tabPlayer
import dev.slne.surf.tab.velocity.util.toTabProfile
import dev.slne.surf.tab.velocity.util.velocityPlayer
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
        val tabMode = tabConfig.config().displayMode

        when (tabMode) {
            TabDisplayMode.PER_PLAYER -> {
                showEntry(
                    player, TabEntryImpl(
                        velocityPlayer.gameProfile.toTabProfile(),
                        tabConfig.config().displayName.formatWithAdventure(
                            velocityPlayer,
                            velocityPlayer
                        ),
                        TabGameMode.CREATIVE,
                        velocityPlayer.ping.toInt(),
                        luckPermsService.getWeight(velocityPlayer.tabPlayer())
                    )
                )
            }

            TabDisplayMode.PER_WORLD -> logger().atWarning()
                .log("TabDisplayMode PER_WORLD is not supported on Velocity!")

            TabDisplayMode.PER_SERVER -> {
                val server = velocityPlayer.currentServer.getOrNull()?.server ?: return
                server.playersConnected.forEach { online ->
                    val display =
                        tabConfig.config().displayName.formatWithAdventure(online, velocityPlayer)

                    val entry = TabEntryImpl(
                        online.gameProfile.toTabProfile(),
                        display,
                        TabGameMode.CREATIVE,
                        online.ping.toInt(),
                        luckPermsService.getWeight(online.tabPlayer())
                    )

                    showEntry(player, entry)
                }
            }

            TabDisplayMode.PER_PROXY -> {
                plugin.proxy.allPlayers.forEach { online ->
                    val display =
                        tabConfig.config().displayName.formatWithAdventure(online, velocityPlayer)

                    val entry = TabEntryImpl(
                        online.gameProfile.toTabProfile(),
                        display,
                        TabGameMode.CREATIVE, online.ping.toInt(),
                        luckPermsService.getWeight(online.tabPlayer())
                    )

                    showEntry(player, entry)
                }
            }

            TabDisplayMode.CLOUD_GLOBAL -> logger().atWarning()
                .log("TabDisplayMode CLOUD_GLOBAL is not supported on Velocity!")
        }
    }

    override fun sendHeader(player: TabPlayer) {
        val velocityPlayer = player.velocityPlayer() ?: return
        val header = tabConfig.config().header.formatWithAdventure(velocityPlayer)

        velocityPlayer.sendPlayerListHeader(header)
    }

    override fun sendFooter(player: TabPlayer) {
        val velocityPlayer = player.velocityPlayer() ?: return
        val footer = tabConfig.config().footer.formatWithAdventure(velocityPlayer)

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
        associatedWithEntry: UUID
    ) {

    }
}