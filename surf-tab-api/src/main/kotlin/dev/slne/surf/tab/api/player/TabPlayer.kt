package dev.slne.surf.tab.api.player

import java.util.UUID

interface TabPlayer {
    val name: String
    val uniqueId: UUID
}