package dev.slne.surf.tab.velocity.factory

import com.google.auto.service.AutoService
import com.velocitypowered.api.proxy.Player
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.core.entity.TabPlayerImpl
import dev.slne.surf.tab.core.factory.TabPlayerFactory
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(TabPlayerFactory::class)
class VelocityTabPlayerFactory : TabPlayerFactory, Services.Fallback {
    override fun createPlayer(playerObject: Any): TabPlayer {
        require(playerObject is Player)
        return createPlayer(playerObject.username, playerObject.uniqueId)
    }

    override fun createPlayer(
        name: String,
        uniqueId: UUID
    ) = TabPlayerImpl(name, uniqueId)
}