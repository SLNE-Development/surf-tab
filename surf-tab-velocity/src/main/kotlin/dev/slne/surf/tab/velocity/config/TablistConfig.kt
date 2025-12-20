package dev.slne.surf.tab.velocity.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TablistConfig(
    val updateEveryMinute: Boolean = true,
    val header: String = "<#6EA6D9>            CASTCRAFTER            <br><#6EA6D9>COMMUNITY SERVER<br><br><#59CCF2><cloud_date><gray> - <#59CCF2><cloud_time> <gray><> <#59CCF2><cloud_online_players><gray>/<#59CCF2><cloud_max_players><br><br>",
    val footer: String = "<br><#59CCF2>Du befindest dich auf <#f9c353><cloud_server><br><#6EA6D9>ᴄᴀѕᴛᴄʀᴀꜰᴛᴇʀ.ᴅᴇ",
    val nameFormat: String = "<luckperms_prefix><cloud_name><luckperms_suffix>",
    val groups: List<TablistGroupConfig> = listOf(
        TablistGroupConfig(
            "testserver",
            listOf("test-server01", "cloud-test02")
        )
    )
)
