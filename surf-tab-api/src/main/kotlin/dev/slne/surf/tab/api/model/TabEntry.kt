package dev.slne.surf.tab.api.model

import net.kyori.adventure.text.Component
import java.util.UUID

interface TabEntry {
    val associatedPlayer: UUID
    val display: Component
    val gameMode: TabGameMode
    val ping: Int
}