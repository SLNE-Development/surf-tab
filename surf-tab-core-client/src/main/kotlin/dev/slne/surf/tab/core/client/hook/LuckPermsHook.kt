package dev.slne.surf.tab.core.client.hook

import dev.slne.surf.tab.core.client.platform.TabPlatform
import dev.slne.surf.tab.core.client.service.tablistService
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.model.user.User
import java.util.*

object LuckPermsHook {
    val luckPerms by lazy {
        LuckPermsProvider.get()
    }

    fun getPrefix(player: UUID) =
        getUser(player)?.primaryGroup?.let {
            luckPerms.groupManager.getGroup(it)?.cachedData?.metaData?.prefix ?: ""
        } ?: ""

    fun getSuffix(player: UUID) =
        getUser(player)?.primaryGroup?.let {
            luckPerms.groupManager.getGroup(it)?.cachedData?.metaData?.suffix ?: ""
        } ?: ""

    fun getWeight(player: UUID) =
        getUser(player)?.primaryGroup?.let {
            luckPerms.groupManager.getGroup(it)?.weight?.orElse(0) ?: 0
        } ?: 0

    fun updatePlayerInTablist(user: User) {
        TabPlatform.launch {
            TabPlatform.player(user.uniqueId)?.let {
                tablistService.requestFormat(it)
            }
        }
    }

    private fun getUser(uuid: UUID) = luckPerms.userManager.getUser(uuid)
}
