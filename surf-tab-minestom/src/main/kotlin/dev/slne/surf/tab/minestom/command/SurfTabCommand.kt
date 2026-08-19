package dev.slne.surf.tab.minestom.command

import dev.slne.minestom.lobby.api.command.commandapi.dsl.anyExecutor
import dev.slne.minestom.lobby.api.command.commandapi.dsl.commandTree
import dev.slne.minestom.lobby.api.command.commandapi.dsl.literalArgument
import dev.slne.surf.tab.core.client.command.reloadTablist
import dev.slne.surf.tab.core.client.command.sendTablistReloaded
import dev.slne.surf.tab.core.client.permission.TabPermissions

fun surfTabCommand() = commandTree("surftab") {
    withPermission(TabPermissions.COMMAND_SURFTAB)

    literalArgument("reload") {
        anyExecutor { executor, _ ->
            reloadTablist()

            executor.sendTablistReloaded()
        }
    }
}
