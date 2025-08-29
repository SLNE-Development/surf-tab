package dev.slne.surf.tab.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.tab.api.TabEntry
import dev.slne.surf.tab.api.player.TabPlayer
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface TabService {
    fun sendTablistUpdate(player: TabPlayer)
    fun clearActualTablist(player: TabPlayer)
    fun sendFakeTablist(player: TabPlayer)

    fun sendHeader(player: TabPlayer)
    fun sendFooter(player: TabPlayer)

    fun showEntry(player: TabPlayer, entry: TabEntry)
    fun showEntries(player: TabPlayer, entries: ObjectSet<TabEntry>) =
        entries.forEach { showEntry(player, it) }

    fun hideEntry(player: TabPlayer, entry: UUID)
    fun hideEntries(player: TabPlayer, entries: ObjectSet<UUID>) =
        entries.forEach { hideEntry(player, it) }

    fun updateEntry(player: TabPlayer, associatedWithEntry: UUID, block: (TabEntry) -> Unit)
    fun updateEntries(
        player: TabPlayer,
        associatedWithEntries: ObjectSet<UUID>,
        block: (TabEntry) -> Unit
    ) =
        associatedWithEntries.forEach { updateEntry(player, it, block) }

    companion object {
        val INSTANCE = requiredService<TabService>()
    }
}

val tabService get() = TabService.INSTANCE