package dev.slne.surf.tab.api

import dev.slne.surf.tab.api.player.TabGameMode
import net.kyori.adventure.text.Component
import java.util.*

interface TabEntry {
    val associatedName: String
    val associatedPlayer: UUID
    val display: Component
    val gameMode: TabGameMode
    val ping: Int
    val weight: Int
}