package dev.slne.surf.tab.minestom.command

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.command.CommandRegistrar

/**
 * Registers the tablist commands of this plugin.
 */
class TabCommandRegistrar @Inject constructor() : CommandRegistrar {
    override fun register() {
        surfTabCommand()
    }
}
