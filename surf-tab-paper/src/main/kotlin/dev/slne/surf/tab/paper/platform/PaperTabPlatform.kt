package dev.slne.surf.tab.paper.platform

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.tab.core.client.platform.TabPlatform
import dev.slne.surf.tab.paper.hook.VanishHook
import dev.slne.surf.tab.paper.isClansHook
import dev.slne.surf.tab.paper.isContentCreatorHook
import dev.slne.surf.tab.paper.isPlaytimeHook
import dev.slne.surf.tab.paper.isVanishHook
import dev.slne.surf.tab.paper.plugin
import kotlinx.coroutines.CoroutineScope
import org.bukkit.Bukkit
import java.util.*

class PaperTabPlatform : TabPlatform {
    override val dataPath get() = plugin.dataPath

    override val playtimeAvailable get() = isPlaytimeHook
    override val clanAvailable get() = isClansHook
    override val contentCreatorAvailable get() = isContentCreatorHook

    override fun onlinePlayers() = Bukkit.getOnlinePlayers().map { PaperTabPlayer(it) }

    override fun onlinePlayerCount() = Bukkit.getOnlinePlayers().size

    override fun player(playerUuid: UUID) = Bukkit.getPlayer(playerUuid)?.let { PaperTabPlayer(it) }

    override fun maxPlayerCount() = Bukkit.getMaxPlayers()

    override fun isVanished(playerUuid: UUID) =
        isVanishHook && VanishHook.isVanished(playerUuid)

    override fun launch(block: suspend CoroutineScope.() -> Unit) = plugin.launch(block = block)
}
