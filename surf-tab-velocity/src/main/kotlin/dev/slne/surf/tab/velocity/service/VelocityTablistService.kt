package dev.slne.surf.tab.velocity.service

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams
import com.google.auto.service.AutoService
import dev.slne.surf.tab.api.model.TabEntry
import dev.slne.surf.tab.api.model.TabGameMode
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.core.model.TabEntryImpl
import dev.slne.surf.tab.core.service.TabService
import dev.slne.surf.tab.core.service.luckPermsService
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.tabConfig
import dev.slne.surf.tab.velocity.util.formatMiniMessage
import dev.slne.surf.tab.velocity.util.tabPlayer
import dev.slne.surf.tab.velocity.util.toPeGameMode
import dev.slne.surf.tab.velocity.util.velocityPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.util.Services
import java.util.*

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
        velocityPlayer.tabList.entries.forEach { it.isListed = false }
    }

    override fun sendFakeTablist(player: TabPlayer) {
        plugin.proxy.allPlayers.forEach { online ->
            tabConfig.config().displayName.formatMiniMessage(online.uniqueId).thenAccept {
                val entry = TabEntryImpl(
                    online.uniqueId, online.username, it,
                    TabGameMode.CREATIVE, online.ping.toInt(),
                    luckPermsService.getWeight(online.tabPlayer())
                )

                println("associatedPlayer: ${entry.associatedPlayer} WEIGHT: ${entry.weight}")
                showEntry(player, entry)
            }
        }
    }

    override fun sendHeader(player: TabPlayer) {
        val velocityPlayer = player.velocityPlayer() ?: return

        tabConfig.config().header.formatMiniMessage(player.uniqueId).thenAccept {
            velocityPlayer.sendPlayerListHeader(it)
        }
    }

    override fun sendFooter(player: TabPlayer) {
        val velocityPlayer = player.velocityPlayer() ?: return

        tabConfig.config().footer.formatMiniMessage(player.uniqueId).thenAccept {
            velocityPlayer.sendPlayerListFooter(it)
        }
    }

    override fun showEntry(
        player: TabPlayer,
        entry: TabEntry
    ) {
        val velocityPlayer = player.velocityPlayer() ?: return
        val addPlayerPacket = WrapperPlayServerPlayerInfoUpdate(
            EnumSet.of(
                WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED,
                WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY
            ),
            WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
                UserProfile(
                    entry.associatedPlayer, "fake-${entry.associatedPlayer}"
                ),
                true,
                entry.ping,
                entry.gameMode.toPeGameMode(),
                entry.display,
                null,
                0
            )
        )

        val teamName = "${entry.weight}-${entry.associatedName}"

        val teamPacket = WrapperPlayServerTeams(
            teamName,
            WrapperPlayServerTeams.TeamMode.CREATE,
            WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.text(entry.associatedName),
                null,
                null,
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.ALWAYS,
                NamedTextColor.RED,
                WrapperPlayServerTeams.OptionData.NONE
            ),
            entry.associatedName
        )
        val teamUpdatePacket = WrapperPlayServerTeams(
            teamName,
            WrapperPlayServerTeams.TeamMode.UPDATE,
            WrapperPlayServerTeams.ScoreBoardTeamInfo(
                entry.display,
                null,
                null,
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.ALWAYS,
                NamedTextColor.RED,
                WrapperPlayServerTeams.OptionData.NONE
            ),
            entry.associatedName
        )
        val nullInfo: WrapperPlayServerTeams.ScoreBoardTeamInfo? = null

        val teamAddPacket = WrapperPlayServerTeams(
            teamName,
            WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
            nullInfo,
            entry.associatedName
        )

        println("associatedPlayer: ${entry.associatedPlayer} WEIGHT: ${entry.weight}: $teamName")

        PacketEvents.getAPI().playerManager.sendPacket(velocityPlayer, addPlayerPacket)
        PacketEvents.getAPI().playerManager.sendPacket(velocityPlayer, teamPacket)
        PacketEvents.getAPI().playerManager.sendPacket(velocityPlayer, teamUpdatePacket)
        PacketEvents.getAPI().playerManager.sendPacket(velocityPlayer, teamAddPacket)
    }

    override fun hideEntry(
        player: TabPlayer,
        entry: TabEntry
    ) {
        val velocityPlayer = player.velocityPlayer() ?: return
        val removePlayerPacket = WrapperPlayServerPlayerInfoRemove(
            entry.associatedPlayer
        )

        PacketEvents.getAPI().playerManager.sendPacket(velocityPlayer, removePlayerPacket)
    }

    override fun updateEntry(
        player: TabPlayer,
        associatedWithEntry: UUID
    ) {

    }
}