package dev.slne.surf.tab.velocity.command

import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.tab.core.common.netty.packets.serverbound.ServerboundReloadPacket

fun surfTabCommand() = commandTree("surftab") {
    withPermission("surf.tab.command.surftab")

    literalArgument("reload") {
        anyExecutor { executor, _ ->
            ServerboundReloadPacket().fireAndForget()

            executor.sendText {
                appendPrefix()
                success("Die Tablist wurde neu geladen.")
            }
        }
    }
}