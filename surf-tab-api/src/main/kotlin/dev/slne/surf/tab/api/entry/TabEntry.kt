package dev.slne.surf.tab.api.entry

import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
abstract class TabEntry(
    val profile: TabProfile,
    val displayName: Component,
    val gameMode: TabGameMode,
    val ping: Int,
    val weight: Int
)