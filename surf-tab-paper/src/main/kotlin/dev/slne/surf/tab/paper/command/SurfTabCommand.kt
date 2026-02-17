package dev.slne.surf.tab.paper.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.tab.paper.service.tablistService
import dev.slne.surf.tab.paper.tablistConfiguration
import org.bukkit.Bukkit

fun surfTabCommand() = commandTree("surftab") {
    withPermission("surf.tab.command.surftab")

    literalArgument("reload") {
        anyExecutor { executor, _ ->
            tablistConfiguration.reload()

            Bukkit.getOnlinePlayers().forEach {
                tablistService.sendAdditions(it)
                tablistService.formatPlayer(it)
            }

            executor.sendText {
                appendSuccessPrefix()
                success("Die Tablist wurde neu geladen.")
            }
        }
    }
}