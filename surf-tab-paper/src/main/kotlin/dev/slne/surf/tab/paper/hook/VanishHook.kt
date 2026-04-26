package dev.slne.surf.tab.paper.hook

import dev.slne.surf.vanish.api.SurfVanishApi
import org.bukkit.Bukkit
import java.util.*

object VanishHook {
    fun isVanished(playerUuid: UUID) =
        Bukkit.getPlayer(playerUuid)?.let { SurfVanishApi.isVanished(it) } ?: false
}