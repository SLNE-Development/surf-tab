package dev.slne.surf.tab.core.common

import dev.slne.surf.cloud.api.common.sync.SyncSet
import dev.slne.surf.tab.api.entry.TabEntry

object SyncValues {
    val tabEntries = SyncSet<Pair<String, TabEntry>>("tab:entries")

    fun init() {}
}