package dev.slne.surf.tab.api.entry

import dev.slne.surf.surfapi.core.api.serializer.adventure.component.SerializableComponent
import kotlinx.serialization.Serializable

@Serializable
data class TabEntry(
    val profile: TabProfile,
    val displayName: SerializableComponent,
    val gameMode: TabGameMode,
    val ping: Int,
    val weight: Int
)