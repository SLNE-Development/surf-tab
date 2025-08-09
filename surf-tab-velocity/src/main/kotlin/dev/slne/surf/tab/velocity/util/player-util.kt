package dev.slne.surf.tab.velocity.util

import com.github.retrooper.packetevents.protocol.player.GameMode
import com.velocitypowered.api.proxy.Player
import dev.slne.surf.tab.api.model.TabGameMode
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.core.factory.tabPlayerFactory
import dev.slne.surf.tab.velocity.plugin
import kotlin.jvm.optionals.getOrNull

fun Player.tabPlayer() = tabPlayerFactory.createPlayer(this)
fun TabPlayer.velocityPlayer() = plugin.proxy.getPlayer(this.uniqueId).getOrNull()

fun TabGameMode.toPeGameMode() = when (this) {
    TabGameMode.SURVIVAL -> GameMode.SURVIVAL
    TabGameMode.CREATIVE -> GameMode.CREATIVE
    TabGameMode.ADVENTURE -> GameMode.ADVENTURE
    TabGameMode.SPECTATOR -> GameMode.SPECTATOR
}