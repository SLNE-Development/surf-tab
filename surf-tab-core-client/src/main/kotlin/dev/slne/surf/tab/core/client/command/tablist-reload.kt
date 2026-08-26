package dev.slne.surf.tab.core.client.command

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.tab.core.client.config.tablistConfiguration
import dev.slne.surf.tab.core.client.platform.TabPlatform
import dev.slne.surf.tab.core.client.service.TablistService
import net.kyori.adventure.audience.Audience

/**
 * Reloads the tablist configuration and applies it to everyone currently online.
 */
fun reloadTablist() {
    tablistConfiguration.reload()

    TabPlatform.launch {
        TablistService.refreshAll()
    }
}

fun Audience.sendTablistReloaded() = sendText {
    appendSuccessPrefix()
    success("Die Tablist wurde neu geladen.")
}
