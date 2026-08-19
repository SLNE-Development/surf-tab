package dev.slne.surf.tab.core.client.platform

import dev.slne.surf.api.core.util.requiredService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.nio.file.Path
import java.util.*

private val platform = requiredService<TabPlatform>()

/**
 * The platform specific calls the shared tablist logic depends on.
 */
interface TabPlatform {

    /**
     * The directory this plugin keeps its files in.
     */
    val dataPath: Path

    /**
     * Whether the afk state of a player can be looked up on this server.
     */
    val playtimeAvailable: Boolean

    /**
     * Whether clan tags can be looked up on this server.
     */
    val clanAvailable: Boolean

    /**
     * Whether live tags can be looked up on this server.
     */
    val contentCreatorAvailable: Boolean

    /**
     * Every player currently on this server.
     */
    fun onlinePlayers(): Collection<TabPlayer>

    /**
     * Returns the player with the given [playerUuid], or `null` if they are not on this server.
     */
    fun player(playerUuid: UUID): TabPlayer?

    /**
     * The number of players this server has room for.
     */
    fun maxPlayerCount(): Int

    /**
     * Whether [playerUuid] is currently hidden from the other players on this server.
     */
    fun isVanished(playerUuid: UUID): Boolean

    /**
     * Launches [block] on the scope the platform applies tablist changes on.
     */
    fun launch(block: suspend CoroutineScope.() -> Unit): Job

    companion object : TabPlatform by platform
}
