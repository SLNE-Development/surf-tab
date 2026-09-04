package dev.slne.surf.tab.core.client.hook

import dev.slne.surf.api.core.luckperms.LuckPermsAccess
import dev.slne.surf.tab.core.client.service.TablistService
import net.luckperms.api.model.user.User
import java.util.*

object LuckPermsHook {

    fun getWeight(player: UUID): Int {
        val user = LuckPermsAccess.getUser(player) ?: return 0

        return user.getInheritedGroups(user.queryOptions)
            .maxOfOrNull { it.weight.orElse(0) }
            ?: 0
    }

    fun updatePlayerInTablist(user: User) = TablistService.updateEntry(user.uniqueId)
}
