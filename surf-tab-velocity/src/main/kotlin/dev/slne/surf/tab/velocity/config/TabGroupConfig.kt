package dev.slne.surf.tab.velocity.config

import it.unimi.dsi.fastutil.objects.ObjectSet
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TabGroupConfig(
    val name: String,
    val velocityServers: ObjectSet<String>
)

@ConfigSerializable
data class TabGroupsConfig(
    val groups: ObjectSet<TabGroupConfig> = ObjectSet.of()
)
