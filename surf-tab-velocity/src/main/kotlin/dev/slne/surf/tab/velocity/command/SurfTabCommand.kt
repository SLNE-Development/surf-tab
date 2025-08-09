package dev.slne.surf.tab.velocity.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.tab.core.TabPermissions

fun surfTabCommand() = commandAPICommand("surfTab") {
    withPermission(TabPermissions.COMMAND_TAB)
    withAliases("tab")

    surfTabRefreshCommand()
    surfTabReloadCommand()
}