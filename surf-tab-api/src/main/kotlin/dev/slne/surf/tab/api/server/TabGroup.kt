package dev.slne.surf.tab.api.server

import it.unimi.dsi.fastutil.objects.ObjectSet

interface TabGroup {
    val name: String
    val display: String

    val servers: ObjectSet<TabServer>
}