package dev.slne.surf.tab.paper.hook

import dev.slne.clan.api.Clan
import dev.slne.clan.api.clan.listener.ClanCreatedListener
import dev.slne.clan.api.surfClanApi
import dev.slne.surf.tab.paper.service.tablistService
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.util.*

object ClanHook {
    fun getClanTag(playerUuid: UUID): Component = surfClanApi.renderClanTag(playerUuid, 10)

    fun createListeners() {
        Clan.addClanModificationListener(object : ClanCreatedListener {
            override fun onClanCreated(clan: Clan) {
                clan.members.map { member -> member.uuid }.mapNotNull { Bukkit.getPlayer(it) }
                    .forEach { member ->
                        tablistService.formatPlayer(member)
                    }
            }
        })

        Clan.registerListener(object : ClanUpdatedListener {
            override fun onClanUpdated(clan: Clan) {
                clan.members.map { member -> member.uuid }.mapNotNull { Bukkit.getPlayer(it) }
                    .forEach { member ->
                        tablistService.formatPlayer(member)
                    }
            }
        })

        Clan.registerListener(object : ClanUpdateMemberListener {
            override fun onClanMemberUpdated(clan: Clan, memberUuid: UUID, added: Boolean) {
                clan.members.map { member -> member.uuid }.mapNotNull { Bukkit.getPlayer(it) }
                    .forEach { member ->
                        tablistService.formatPlayer(member)
                    }
            }
        })

        Clan.registerListener(object : ClanDeletedListener {
            override fun onClanDeleted(clan: ClanView) {
                clan.members.map { member -> member.uuid }.mapNotNull { Bukkit.getPlayer(it) }
                    .forEach { member ->
                        tablistService.formatPlayer(member)
                    }
            }
        })
    }
}