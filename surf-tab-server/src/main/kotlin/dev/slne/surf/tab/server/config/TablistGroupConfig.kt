package dev.slne.surf.tab.server.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TablistGroupConfig(
    val name: String = "example",
    val clients: List<String> = mutableListOf("lobby01", "lobby02"),
)
