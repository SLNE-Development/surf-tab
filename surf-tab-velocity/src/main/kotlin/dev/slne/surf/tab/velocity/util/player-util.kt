package dev.slne.surf.tab.velocity.util

import com.velocitypowered.api.proxy.player.TabList
import com.velocitypowered.api.proxy.player.TabListEntry
import com.velocitypowered.api.util.GameProfile
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.tab.api.entry.TabEntry
import dev.slne.surf.tab.api.entry.TabProfile
import dev.slne.surf.tab.api.entry.TabProfileProperty
import dev.slne.surf.tab.velocity.plugin
import kotlin.jvm.optionals.getOrNull

fun TabProfile.toGameProfile() = GameProfile(this.uuid, this.name, this.properties.map {
    GameProfile.Property(it.name, it.value, it.signature)
})

fun TabEntry.toVelocity(tablist: TabList): TabListEntry = TabListEntry
    .builder()
    .tabList(tablist)
    .profile(this.profile.toGameProfile())
    .displayName(this.displayName)
    .latency(this.ping)
    .listed(true)
    .listOrder(this.weight)
    .showHat(true)
    .gameMode(this.gameMode.id)
    .build()

fun GameProfile.toTabProfile() = TabProfile(
    uuid = this.id,
    name = this.name,
    properties = this.properties.map {
        TabProfileProperty(
            name = it.name,
            value = it.value,
            signature = it.signature ?: ""
        )
    }
)

val CloudPlayer.currentPlatform get() = plugin.proxy.getPlayer(this.uuid).getOrNull()