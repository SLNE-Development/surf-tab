package dev.slne.surf.tab.api

import dev.slne.surf.tab.api.auth.TabGameProfile
import dev.slne.surf.tab.api.player.TabGameMode
import net.kyori.adventure.text.Component

interface TabEntry {
    val profile: TabGameProfile
    val display: Component
    val gameMode: TabGameMode
    val ping: Int
    val weight: Int
}