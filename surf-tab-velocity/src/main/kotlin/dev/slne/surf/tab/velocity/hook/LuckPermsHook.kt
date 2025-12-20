package dev.slne.surf.tab.velocity.hook

import dev.slne.surf.tab.velocity.plugin
import net.luckperms.api.LuckPermsProvider
import java.util.*

object LuckPermsHook {
    fun isEnabled() = plugin.proxy.pluginManager.isLoaded("luckperms")

    private val luckPerms by lazy {
        LuckPermsProvider.get()
    }

    fun getPrefix(player: UUID) =
        if (isEnabled()) luckPerms.userManager.getUser(player)?.cachedData?.metaData?.prefix
            ?: "" else ""

    fun getSuffix(player: UUID) =
        if (isEnabled()) luckPerms.userManager.getUser(player)?.cachedData?.metaData?.suffix
            ?: "" else ""

    fun getWeight(player: UUID) =
        if (isEnabled()) luckPerms.userManager.getUser(player)?.cachedData?.metaData?.getMetaValue("weight")
            ?.toInt() ?: 0 else 0
}