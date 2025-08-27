package dev.slne.surf.tab.velocity.config

import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.tab.api.TabDisplayMode
import dev.slne.surf.tab.velocity.util.mm
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TabConfig(
    val header: String = "<br>LOGO<br><br>${Colors.INFO.mm()}%localtime_time_dd:MM:yyyy% ${Colors.SPACER.mm()}-${Colors.INFO.mm()} %localtime_time_HH:mm:ss% ${Colors.SPACER.mm()}<> ${Colors.INFO.mm()}%server_online% ${Colors.SPACER.mm()}/ ${Colors.INFO.mm()}%server_max_online%<br><br>",
    val footer: String = "<br><br>${Colors.INFO.mm()}Server${Colors.SPACER.mm()}: ${Colors.VARIABLE_VALUE.mm()}IDK <br><br>%player_ping%ms ${Colors.SPACER.mm()}<> %server_tps_1_colored% ${Colors.SPACER.mm()}/ %server_tps_5_colored% ${Colors.SPACER.mm()}/ %server_tps_15_colored%<br><br><br>",
    val displayName: String = "%luckperms_prefix% %player_name% %luckperms_suffix%",
    val displayMode: TabDisplayMode = TabDisplayMode.PER_PROXY
)
