package dev.slne.surf.tab.velocity.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TabGroupConfig(
    val name: String,
    val velocityServers: List<String>
)

@ConfigSerializable
data class TabGroupsConfig(
    val groups: List<TabGroupConfig> = mutableListOf(
        TabGroupConfig(
            "example",
            listOf("example01", "example02")
        )
    )
)
