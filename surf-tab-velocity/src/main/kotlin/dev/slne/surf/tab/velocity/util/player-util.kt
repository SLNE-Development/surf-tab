package dev.slne.surf.tab.velocity.util

import com.velocitypowered.api.proxy.Player
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.core.factory.tabPlayerFactory
import dev.slne.surf.tab.velocity.plugin
import kotlin.jvm.optionals.getOrNull

fun Player.tabPlayer() = tabPlayerFactory.createPlayer(this)
fun TabPlayer.velocityPlayer() = plugin.proxy.getPlayer(this.uniqueId).getOrNull()