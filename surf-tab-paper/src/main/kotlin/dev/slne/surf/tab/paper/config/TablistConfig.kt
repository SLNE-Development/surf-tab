package dev.slne.surf.tab.paper.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TablistConfig(
    val header: String = "<br><#6EA6D9>                        CASTCRAFTER                        <br><#6EA6D9>COMMUNITY SERVER<br><br><#59CCF2><date><gray> - <#59CCF2><time> <gray><> <#59CCF2><players_online><gray> / <#59CCF2><players_max><br><br>",
    val footer: String = "<br><#59CCF2>Du befindest dich auf <#f9c353><server><br><#6EA6D9>ᴄᴀѕᴛᴄʀᴀꜰᴛᴇʀ.ᴅᴇ<br>"
)
