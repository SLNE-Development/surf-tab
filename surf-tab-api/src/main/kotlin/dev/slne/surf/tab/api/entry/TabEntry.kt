package dev.slne.surf.tab.api.entry

import dev.slne.surf.tab.api.serializer.ComponentSerializer
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
data class TabEntry(
    val profile: TabProfile,
    val displayName: @Serializable(with = ComponentSerializer::class) Component,
    val gameMode: TabGameMode,
    val ping: Int,
    val weight: Int
)