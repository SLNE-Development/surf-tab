package dev.slne.surf.tab.api.model

import net.kyori.adventure.text.Component
import java.util.*

interface TabEntry {
    val associatedPlayer: UUID
    val display: Component
    val gameMode: TabGameMode
    val ping: Int
    val weight: Int
}