package dev.slne.surf.tab.core.client.hook

import dev.slne.surf.content.creator.api.ContentCreatorApi
import dev.slne.surf.content.creator.api.ContentCreatorPlatform
import dev.slne.surf.content.creator.api.listener.StateChangeListener
import dev.slne.surf.content.creator.api.platform.PlatformState
import dev.slne.surf.tab.core.client.platform.TabPlatform
import dev.slne.surf.tab.core.client.service.tablistService
import net.kyori.adventure.text.Component
import java.util.*

object ContentCreatorHook {
    fun renderLiveTag(playerUuid: UUID): Component =
        ContentCreatorApi.renderLiveTag(playerUuid, space = true)

    fun registerListener() {
        ContentCreatorApi.registerStateChangeListener(object : StateChangeListener {
            override fun onStateChanged(
                playerUuid: UUID,
                contentCreatorPlatform: ContentCreatorPlatform,
                newState: PlatformState
            ) {
                TabPlatform.launch {
                    TabPlatform.player(playerUuid)?.let {
                        tablistService.requestFormat(it)
                    }
                }
            }
        })
    }
}
