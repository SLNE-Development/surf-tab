package dev.slne.surf.tab.velocity.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.tab.core.TabPermissions
import dev.slne.surf.tab.core.service.tabGroupService
import dev.slne.surf.tab.core.service.tabService
import dev.slne.surf.tab.velocity.plugin
import dev.slne.surf.tab.velocity.tabConfig
import dev.slne.surf.tab.velocity.tabGroupConfig
import dev.slne.surf.tab.velocity.util.tabPlayer
import kotlin.system.measureTimeMillis

fun CommandAPICommand.surfTabReloadCommand() = subcommand("reload") {
    withPermission(TabPermissions.COMMAND_TAB_RELOAD)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendPrefix()
            info("Das Plugin wird neu geladen...")

            val ms = measureTimeMillis {
                tabConfig.reload()
                tabGroupConfig.reload()
                tabGroupService.loadGroups(false)
                plugin.proxy.allPlayers.forEach {
                    tabService.sendTablistUpdate(it.tabPlayer())
                }
            }

            executor.sendText {
                appendPrefix()
                success("Das Plugin wurde erfolgreich neu geladen. ")
                spacer("(${ms}ms)")
            }
        }
    }
}