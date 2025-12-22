package dev.slne.surf.tab.velocity.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.service.tablistService
import dev.slne.surf.tab.velocity.tablistConfiguration

fun surfTabCommand() = commandTree("surftab") {
    withPermission("surf.tab.command.surftab")

    literalArgument("reload") {
        anyExecutor { executor, _ ->
            tablistConfiguration.reload()

            plugin.proxy.allPlayers.forEach {
                tablistService.updatePlayerInTablist(it)
            }

            executor.sendText {
                appendPrefix()
                success("Die Tablist wurde neu geladen.")
            }
        }
    }
}