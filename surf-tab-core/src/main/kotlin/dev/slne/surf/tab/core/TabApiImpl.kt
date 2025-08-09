package dev.slne.surf.tab.core

import com.google.auto.service.AutoService
import dev.slne.surf.tab.api.TabApi
import dev.slne.surf.tab.core.factory.tabPlayerFactory
import net.kyori.adventure.util.Services

@AutoService(TabApi::class)
class TabApiImpl : TabApi, Services.Fallback {
    override fun getPlayer(playerObject: Any) = tabPlayerFactory.createPlayer(playerObject)
}