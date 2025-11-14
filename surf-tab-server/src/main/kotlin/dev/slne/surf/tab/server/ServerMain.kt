package dev.slne.surf.tab.server

import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.server.netty.packet.broadcast
import dev.slne.surf.cloud.api.server.plugin.StandalonePlugin
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.tab.core.common.netty.packets.clientbound.ClientboundTablistAdditionsPacket
import dev.slne.surf.tab.server.config.TablistConfigProvider
import dev.slne.surf.tab.server.placeholder.PlaceholderManager
import dev.slne.surf.tab.server.placeholder.impl.CloudPlaceholderExtension
import dev.slne.surf.tab.server.placeholder.impl.LuckPermsPlaceholderExtension
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit

class ServerMain : StandalonePlugin() {
    val log = logger()

    override suspend fun load() {
    }

    override suspend fun enable() {
        PlaceholderManager.register(CloudPlaceholderExtension)
        PlaceholderManager.registerContextual(CloudPlayer::class.java, CloudPlaceholderExtension)
        PlaceholderManager.registerAsyncContextual(
            CloudPlayer::class.java,
            LuckPermsPlaceholderExtension
        )
    }

    override suspend fun disable() {
    }

    val configuration = TablistConfigProvider()

    @Scheduled(fixedRate = 1L, timeUnit = TimeUnit.MINUTES)
    fun updateTablist() {
        if (config.updateEveryMinute) {
            CloudPlayer.all().forEach {
                ClientboundTablistAdditionsPacket(
                    player = it,
                    header = PlaceholderManager.parse(config.header, it),
                    footer = PlaceholderManager.parse(config.footer, it)
                ).broadcast()
            }
        }
    }
}

val config get() = plugin.configuration.config

val plugin get() = StandalonePlugin.getPlugin(ServerMain::class)