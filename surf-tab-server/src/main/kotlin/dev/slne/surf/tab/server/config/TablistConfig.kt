package dev.slne.surf.tab.server.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TablistConfig(
    val updateEveryMinute: Boolean = true,
    val header: String = "<#6EA6D9>CASTCRAFTER.DE<br><br><#40d1db><cloud:date><gray> - <#40d1db><cloud:time><gray><> <#40d1db><cloud:online_players><gray>/<#40d1db><cloud:max_players><br><br>",
    val footer: String = "<#59CCF2>Du befindest dich auf <#f9c353><cloud:server>",
    val nameFormat: String = "<luckperms:prefix> <player_name> <luckperms:suffix>",
    val groups: List<TablistGroupConfig> = mutableListOf()
)
