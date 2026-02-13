package dev.slne.surf.tab.paper.hook

import dev.slne.surf.vanish.api.surfVanishApi
import java.util.*

object SurfVanishHook {
    fun isVanished(playerUuid: UUID) =
        surfVanishApi.onlineVanishedPlayersUuid().contains(playerUuid)
}