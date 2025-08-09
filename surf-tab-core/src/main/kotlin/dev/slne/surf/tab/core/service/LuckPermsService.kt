package dev.slne.surf.tab.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.tab.api.player.TabPlayer

interface LuckPermsService {
    fun getWeight(tabPlayer: TabPlayer): Int
    fun registerListener()

    companion object {
        val INSTANCE = requiredService<LuckPermsService>()
    }
}

val luckPermsService get() = LuckPermsService.INSTANCE