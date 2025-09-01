package dev.slne.surf.tab.velocity.util

import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.player.TabListEntry
import com.velocitypowered.api.util.GameProfile
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import dev.slne.surf.tab.api.auth.TabGameProfile
import dev.slne.surf.tab.api.auth.TabProperty
import dev.slne.surf.tab.api.player.TabGameMode
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.core.model.TabEntryImpl
import dev.slne.surf.tab.core.registry.tabPlayerRegistry
import dev.slne.surf.tab.velocity.plugin
import kotlin.jvm.optionals.getOrNull

fun Player.tabPlayer() = tabPlayerRegistry.getTabPlayer(this.uniqueId)
fun TabPlayer.velocityPlayer() = plugin.proxy.getPlayer(this.uniqueId).getOrNull()

fun GameProfile.toTabProfile() = TabGameProfile(
    this.id,
    this.name,
    this.properties.map { TabProperty(it.name, it.value, it.signature) }.toObjectSet()
)

fun TabGameProfile.toGameProfile() = GameProfile(this.uuid, this.name, this.properties.map {
    GameProfile.Property(it.name, it.value, it.signature)
})

fun TabListEntry.toTabEntry() = TabEntryImpl(
    this.profile.toTabProfile(),
    this.displayNameComponent.getOrNull() ?: error("Display name is not present"),
    TabGameMode.entries.find { it.index == this.gameMode }
        ?: error("Unknown game mode: ${this.gameMode}"),
    this.latency,
    this.listOrder
)