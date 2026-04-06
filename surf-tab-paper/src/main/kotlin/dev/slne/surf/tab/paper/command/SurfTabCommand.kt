package dev.slne.surf.tab.paper.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.tab.paper.plugin
import dev.slne.surf.tab.paper.service.tablistService
import dev.slne.surf.tab.paper.tablistConfiguration
import org.bukkit.Bukkit

fun surfTabCommand() = commandTree("surftab") {
    withPermission("surf.tab.command.surftab")

    literalArgument("reload") {
        anyExecutor { executor, _ ->
            tablistConfiguration.reload()

            plugin.launch {
                Bukkit.getOnlinePlayers().forEach {
                    tablistService.sendAdditions(it)
                    tablistService.formatPlayer(it)
                }
            }

            executor.sendText {
                appendSuccessPrefix()
                success("Die Tablist wurde neu geladen.")
            }
        }
    }
}