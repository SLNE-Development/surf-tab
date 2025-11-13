package dev.slne.surf.tab.api.entry

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class TabProfile(
    val uuid: @Contextual UUID,
    val name: String,
    val properties: List<TabProfileProperty> = listOf<TabProfileProperty>()
)

@Serializable
data class TabProfileProperty(
    val name: String,
    val value: String,
    val signature: String
)
