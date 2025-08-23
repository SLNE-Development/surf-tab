package dev.slne.surf.tab.api.model

import dev.slne.surf.tab.api.player.TabPlayer
import it.unimi.dsi.fastutil.objects.ObjectSet

interface TabServer {
    val name: String
    fun retrievePlayers(): ObjectSet<TabPlayer>
}
