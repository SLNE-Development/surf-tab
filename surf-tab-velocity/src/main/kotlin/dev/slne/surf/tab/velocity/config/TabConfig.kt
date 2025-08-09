package dev.slne.surf.tab.velocity.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TabConfig(
    val header: String = "",
    val footer: String = "",
    val displayName: String = "",
    val displayMode: String = ""
)
