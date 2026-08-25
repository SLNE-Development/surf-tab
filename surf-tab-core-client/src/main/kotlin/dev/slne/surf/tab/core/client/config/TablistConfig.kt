package dev.slne.surf.tab.core.client.config

import dev.slne.surf.api.core.config.constraints.PositiveNumber
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
data class TablistConfig(
    val header: String = "<br><#6EA6D9>                        CASTCRAFTER                        <br><#6EA6D9>COMMUNITY SERVER<br><br><#59CCF2><date><gray> - <#59CCF2><time> <gray><> <#59CCF2><players_online><gray> / <#59CCF2><players_max><br><br>",
    val footer: String = "<br><#59CCF2>Du befindest dich auf <#f9c353><server><br><#6EA6D9>ᴄᴀѕᴛᴄʀᴀꜰᴛᴇʀ.ᴅᴇ<br>",

    @Comment(
        "How often, in seconds, the header and the footer are rendered again while they " +
                "contain placeholders the tablist cannot observe.\n" +
                "\n" +
                "The tablist knows when its own placeholders change and updates the moment they do." +
                "A placeholder some other plugin contributes comes without any such event, so " +
                "a header or footer containing one is looked at on this interval instead of being " +
                "left stale. It only applies to a template that actually contains one.\n" +
                "\n" +
                "Such a template also has to be rendered for every player separately, so lowering " +
                "this is what makes the tablist expensive on a busy server.\n" +
                "\n" +
                "The minimum is 1 second."
    )
    @PositiveNumber
    val unknownPlaceholderRefreshSeconds: Int = 5
)
