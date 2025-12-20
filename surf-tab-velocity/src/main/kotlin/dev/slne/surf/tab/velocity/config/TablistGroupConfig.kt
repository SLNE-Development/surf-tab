package dev.slne.surf.tab.velocity.config

import dev.slne.surf.tab.api.entry.TabGroup
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TablistGroupConfig(
    val name: String = "example",
    val clients: List<String> = listOf("lobby01", "lobby02"),
) {
    fun toTabGroup() = TabGroup(
        name = name,
        clients = clients,
    )
}
