package dev.slne.surf.tab.api.server

import dev.slne.surf.tab.api.player.TabPlayer
import it.unimi.dsi.fastutil.objects.ObjectSet

interface TabGroup {
    val name: String

    fun retrievePlayers(): ObjectSet<TabPlayer>

    val servers: ObjectSet<TabServer>
}