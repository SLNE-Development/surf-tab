package dev.slne.surf.tab.velocity.util

import com.github.retrooper.packetevents.protocol.player.GameMode
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.util.GameProfile
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import dev.slne.surf.tab.api.auth.TabGameProfile
import dev.slne.surf.tab.api.auth.TabProperty
import dev.slne.surf.tab.api.player.TabGameMode
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

fun GameProfile.toTabProfile() = TabGameProfile(
    this.id,
    this.name,
    this.properties.map { TabProperty(it.name, it.value, it.signature) }.toObjectSet()
)

fun TabGameProfile.toGameProfile() = GameProfile(this.uuid, this.name, this.properties.map {
    GameProfile.Property(it.name, it.value, it.signature)
})