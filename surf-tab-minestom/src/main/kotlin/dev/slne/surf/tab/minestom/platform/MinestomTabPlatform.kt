package dev.slne.surf.tab.minestom.platform

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.coroutine.minestomScope
import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.surf.core.api.common.server.SurfServer
import dev.slne.surf.tab.core.client.platform.TabPlatform
import dev.slne.surf.tab.minestom.TabMinestomEntrypoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@AutoService(TabPlatform::class)
class MinestomTabPlatform : TabPlatform {
    override val dataPath get() = TabMinestomEntrypoint.dataPath

    override val playtimeAvailable = true
    override val clanAvailable = true
    override val contentCreatorAvailable = true

    override fun onlinePlayers() = ConnectionManager.onlinePlayers.map { MinestomTabPlayer(it) }

    override fun player(playerUuid: UUID) =
        ConnectionManager.getOnlinePlayerByUuid(playerUuid)?.let { MinestomTabPlayer(it) }

    override fun maxPlayerCount() = SurfServer.current().maxPlayers

    override fun isVanished(playerUuid: UUID) = false

    override fun launch(block: suspend CoroutineScope.() -> Unit) = minestomScope.launch(block = block)
}
