package dev.slne.surf.tab.velocity.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.tab.core.TabPermissions
import dev.slne.surf.tab.core.service.tabService
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.util.tabPlayer

fun CommandAPICommand.surfTabRefreshCommand() = subcommand("refresh") {
    withPermission(TabPermissions.COMMAND_TAB_REFRESH)
    anyExecutor { executor, _ ->
        plugin.proxy.allPlayers.forEach {
            tabService.sendTablistUpdate(it.tabPlayer())
        }

        executor.sendText {
            appendPrefix()
            success("Die Tablist wurde aktualisiert.")
        }
    }
}