package dev.slne.surf.tab.server.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TablistConfig(
    val header: String = "",
    val footer: String = "",
    val nameFormat: String = "",
    val groups: List<TablistGroupConfig> = mutableListOf()
)
