package dev.slne.surf.tab.api.entry

import net.kyori.adventure.text.Component

data class TabEntry(
    val profile: TabProfile,
    val displayName: Component,
    val gameMode: TabGameMode,
    val ping: Int,
    val weight: Int
)