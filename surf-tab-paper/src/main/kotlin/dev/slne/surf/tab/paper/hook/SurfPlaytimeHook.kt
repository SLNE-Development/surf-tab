package dev.slne.surf.tab.paper.hook

import dev.slne.surf.playtime.api.common.surfPlaytimeApi
import java.util.*

object SurfPlaytimeHook {
    fun isAfk(playerUuid: UUID) =
        surfPlaytimeApi.isPlayerAfk(playerUuid)
}