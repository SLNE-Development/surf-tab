package dev.slne.surf.tab.core.client.platform

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.nio.file.Path
import java.util.*

/**
 * The platform specific calls the shared tablist logic depends on.
 */
interface TabPlatform {

    /**
     * The directory this plugin keeps its files in.
     */
    val dataPath: Path

    val playtimeAvailable: Boolean
    val clanAvailable: Boolean
    val contentCreatorAvailable: Boolean

    fun onlinePlayers(): Collection<TabPlayer>

    fun onlinePlayerCount(): Int

    fun player(playerUuid: UUID): TabPlayer?

    fun maxPlayerCount(): Int

    fun isVanished(playerUuid: UUID): Boolean

    /**
     * Launches [block] on the scope the platform applies tablist changes on.
     */
    fun launch(block: suspend CoroutineScope.() -> Unit): Job
}
