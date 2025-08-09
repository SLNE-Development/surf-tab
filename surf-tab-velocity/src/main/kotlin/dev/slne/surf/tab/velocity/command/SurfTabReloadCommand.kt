package dev.slne.surf.tab.velocity.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.tab.core.TabPermissions
import dev.slne.surf.tab.velocity.tabConfig
import kotlin.system.measureTimeMillis

fun CommandAPICommand.surfTabReloadCommand() = subcommand("reload") {
    withPermission(TabPermissions.COMMAND_TAB_RELOAD)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendPrefix()
            info("Das Plugin wird neu geladen...")

            val ms = measureTimeMillis {
                tabConfig.reload()
            }

            executor.sendText {
                appendPrefix()
                success("Das Plugin wurde erfolgreich neu geladen. ")
                spacer("(${ms}ms)")
            }
        }
    }
}