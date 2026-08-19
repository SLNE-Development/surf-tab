package dev.slne.surf.tab.core.client.platform

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import java.util.*

/**
 * A player as the shared tablist logic sees them.
 */
interface TabPlayer {

    val uuid: UUID

    /**
     * The audience the tablist is sent to and placeholders are resolved against.
     */
    val audience: Audience

    /**
     * The name this player's tablist entry is built from.
     */
    fun baseName(): Component

    /**
     * Shows [name] as this player's tablist entry.
     */
    suspend fun showTabName(name: Component)

    /**
     * Sorts this player's tablist entry by [order], where a higher order comes first.
     */
    suspend fun showTabOrder(order: Int)
}
