package dev.slne.surf.tab.velocity.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.tab.core.TabPermissions

fun CommandAPICommand.surfTabRefreshCommand() = subcommand("refresh") {
    withPermission(TabPermissions.COMMAND_TAB_REFRESH)
    anyExecutor { executor, _ ->
        ServerboundRequestTablistUpdatePacket().fireAndForget()

        executor.sendText {
            appendPrefix()
            success("Ein Tablist update wurde angefordert, ich weiß nicht ob es angekommen ist...")
        }
    }
}