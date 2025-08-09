package dev.slne.surf.tab.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.tab.api.player.TabPlayer

interface TabApi {
    fun getPlayer(playerObject: Any): TabPlayer

    companion object {
        val INSTANCE = requiredService<TabApi>()
    }
}

val surfTabApi get() = TabApi.INSTANCE