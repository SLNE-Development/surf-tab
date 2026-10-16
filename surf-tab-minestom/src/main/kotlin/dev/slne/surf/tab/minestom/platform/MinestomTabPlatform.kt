package dev.slne.surf.tab.minestom.platform

import dev.slne.surf.api.minestom.coroutine.minestomScope
import dev.slne.surf.api.minestom.extension.ConnectionManager
import dev.slne.surf.core.api.common.server.SurfServer
import dev.slne.surf.tab.core.client.platform.TabPlatform
import dev.slne.surf.tab.minestom.TabMinestomPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

class MinestomTabPlatform : TabPlatform {
    override val dataPath get() = TabMinestomPlugin.dataDirectory

    override val playtimeAvailable = true
    override val clanAvailable = true
    override val contentCreatorAvailable = true

    override fun onlinePlayers() = ConnectionManager.onlinePlayers.map { MinestomTabPlayer(it) }

    override fun onlinePlayerCount() = ConnectionManager.onlinePlayers.size

    override fun player(playerUuid: UUID) =
        ConnectionManager.getOnlinePlayerByUuid(playerUuid)?.let { MinestomTabPlayer(it) }

    override fun maxPlayerCount() = SurfServer.current().maxPlayers

    override fun isVanished(playerUuid: UUID) = false

    override fun launch(block: suspend CoroutineScope.() -> Unit) = minestomScope.launch(block = block)
}
