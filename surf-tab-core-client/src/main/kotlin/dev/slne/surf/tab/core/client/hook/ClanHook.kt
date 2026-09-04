package dev.slne.surf.tab.core.client.hook

import dev.slne.clan.api.clan.Clan
import dev.slne.clan.api.clan.ClanView
import dev.slne.clan.api.clan.listener.ClanCreatedListener
import dev.slne.clan.api.clan.listener.ClanDeletedListener
import dev.slne.clan.api.clan.listener.ClanUpdateMemberListener
import dev.slne.clan.api.clan.listener.ClanUpdatedListener
import dev.slne.surf.tab.core.client.service.TablistService
import net.kyori.adventure.text.Component
import java.util.*

object ClanHook {
    suspend fun getClanTag(playerUuid: UUID): Component? =
        Clan.byPlayer(playerUuid)?.renderClanTag(10)

    fun createListeners() {
        Clan.registerListener(ClanCreatedListener(::formatMembers))
        Clan.registerListener(ClanUpdatedListener(::formatMembers))
        Clan.registerListener(ClanDeletedListener(::formatMembers))
        Clan.registerListener(ClanUpdateMemberListener { clan, _, _ -> formatMembers(clan) })
    }

    private fun formatMembers(clan: ClanView) {
        for (member in clan.members) {
            TablistService.updateEntry(member.uuid)
        }
    }
}
