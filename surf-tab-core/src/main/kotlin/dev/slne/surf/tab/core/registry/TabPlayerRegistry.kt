package dev.slne.surf.tab.core.registry

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.tab.api.player.TabPlayer
import java.util.UUID

interface TabPlayerRegistry {
    fun getTabPlayer(uuid: UUID): TabPlayer?

    companion object {
        val INSTANCE = requiredService<TabPlayerRegistry>()
    }
}

val tabPlayerRegistry get() = TabPlayerRegistry.INSTANCE