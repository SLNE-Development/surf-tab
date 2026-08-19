package dev.slne.surf.tab.core.client.command

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.tab.core.client.config.tablistConfiguration
import dev.slne.surf.tab.core.client.platform.TabPlatform
import dev.slne.surf.tab.core.client.service.tablistService
import kotlinx.coroutines.launch
import net.kyori.adventure.audience.Audience

/**
 * Reloads the tablist configuration and applies it to everyone currently online.
 */
fun reloadTablist() {
    tablistConfiguration.reload()

    TabPlatform.launch {
        for (player in TabPlatform.onlinePlayers()) {
            launch {
                tablistService.sendAdditions(player)
                tablistService.formatPlayer(player)
            }
        }

        TabPlatform.onlinePlayers().forEach {
            tablistService.sendAdditions(it)
            tablistService.formatPlayer(it)
        }
    }
}

fun Audience.sendTablistReloaded() = sendText {
    appendSuccessPrefix()
    success("Die Tablist wurde neu geladen.")
}
