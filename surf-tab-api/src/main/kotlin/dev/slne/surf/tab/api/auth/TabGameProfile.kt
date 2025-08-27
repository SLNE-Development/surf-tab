package dev.slne.surf.tab.api.auth

import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

data class TabGameProfile(
    val uuid: UUID,
    val name: String,
    val properties: ObjectSet<TabProperty>
)
