package dev.slne.surf.tab.core.client.platform

import net.kyori.adventure.text.Component

/**
 * A player on this server as the shared tablist logic sees them.
 */
interface TabPlayer : TabViewer {

    /**
     * The name this player's tablist entry is built from.
     */
    fun baseName(): Component

    /**
     * Reads [baseName] on the owning entity context when the platform requires it.
     */
    suspend fun baseNameSnapshot(): Component

    /**
     * Shows [name] as this player's tablist entry, sorted by [order] where a higher order comes first.
     */
    suspend fun showTabEntry(name: Component, order: Int)
}
