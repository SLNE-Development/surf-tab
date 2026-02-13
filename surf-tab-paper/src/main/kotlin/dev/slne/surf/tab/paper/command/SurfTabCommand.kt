package dev.slne.surf.tab.paper.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.tab.paper.tablistConfiguration

fun surfTabCommand() = commandTree("surftab") {
    withPermission("surf.tab.command.surftab")

    literalArgument("reload") {
        anyExecutor { executor, _ ->
            tablistConfiguration.reload()

            executor.sendText {
                appendSuccessPrefix()
                success("Die Tablist wurde neu geladen.")
            }
        }
    }
}