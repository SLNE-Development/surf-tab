package dev.slne.surf.tab.minestom.command

import dev.slne.surf.api.minestom.command.dsl.anyExecutor
import dev.slne.surf.api.minestom.command.dsl.commandTree
import dev.slne.surf.api.minestom.command.dsl.literalArgument
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
