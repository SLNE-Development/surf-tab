package dev.slne.surf.tab.core.client.command

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.tab.core.client.service.TablistService
import net.kyori.adventure.audience.Audience

/**
 * Reloads the tablist configuration and applies it to everyone currently online.
 */
fun reloadTablist() = TablistService.reload()

fun Audience.sendTablistReloaded() = sendText {
    appendSuccessPrefix()
    success("Die Tablist wurde neu geladen.")
}
