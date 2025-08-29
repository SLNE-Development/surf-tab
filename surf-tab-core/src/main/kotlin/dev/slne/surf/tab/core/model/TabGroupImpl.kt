package dev.slne.surf.tab.core.model

import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.api.server.TabGroup
import dev.slne.surf.tab.api.server.TabServer
import it.unimi.dsi.fastutil.objects.ObjectSet

class TabGroupImpl(
    override val name: String,
    override val servers: ObjectSet<TabServer>
) : TabGroup {
    override fun retrievePlayers(): ObjectSet<TabPlayer> {
        TODO("Not yet implemented")
    }
}