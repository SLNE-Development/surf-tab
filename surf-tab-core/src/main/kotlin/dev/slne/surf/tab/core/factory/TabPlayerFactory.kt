package dev.slne.surf.tab.core.factory

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.tab.api.player.TabPlayer
import java.util.*

interface TabPlayerFactory {
    fun createPlayer(playerObject: Any): TabPlayer
    fun createPlayer(name: String, uniqueId: UUID): TabPlayer

    companion object {
        val INSTANCE = requiredService<TabPlayerFactory>()
    }
}

val tabPlayerFactory get() = TabPlayerFactory.INSTANCE