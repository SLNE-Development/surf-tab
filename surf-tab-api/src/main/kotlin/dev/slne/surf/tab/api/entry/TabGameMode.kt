package dev.slne.surf.tab.api.entry

import kotlinx.serialization.Serializable

@Serializable
enum class TabGameMode(val id: Int) {
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);
}