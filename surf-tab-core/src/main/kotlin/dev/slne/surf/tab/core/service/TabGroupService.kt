package dev.slne.surf.tab.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import dev.slne.surf.tab.api.player.TabPlayer
import dev.slne.surf.tab.api.server.TabGroup
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface TabGroupService {
    fun loadGroups(notify: Boolean)

    fun getGroup(groupName: String): TabGroup?
    fun getGroups(): ObjectSet<TabGroup>
    fun getGroupForServer(serverName: String): TabGroup?
    fun getGroupForPlayer(uuid: UUID): TabGroup?

    fun retrievePlayers(group: TabGroup): ObjectSet<TabPlayer>

    companion object {
        val INSTANCE = requiredService<TabGroupService>()
    }
}

val tabGroupService get() = TabGroupService.INSTANCE