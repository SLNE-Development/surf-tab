package dev.slne.surf.tab.api.entry

import kotlinx.serialization.Serializable

@Serializable
data class TabGroup(
    val name: String,
    val clients: List<String>
)
