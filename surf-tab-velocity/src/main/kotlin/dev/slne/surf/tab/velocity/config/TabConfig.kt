package dev.slne.surf.tab.velocity.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class TabConfig(
    val header: String = "A example header\nwith multiple lines\n<red>and colors</red>",
    val footer: String = "A example <b>footer, also with <rainbow>colors",
    val displayName: String = "%luckperms_prefix% %player_name% %luckperms_suffix%",
    val displayMode: String = ""
)
