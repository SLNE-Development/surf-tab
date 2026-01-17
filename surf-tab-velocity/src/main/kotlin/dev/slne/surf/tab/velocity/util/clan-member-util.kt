package dev.slne.surf.tab.velocity.util

import dev.slne.clan.api.member.ClanMember
import dev.slne.surf.tab.velocity.plugin

fun ClanMember.isOnline() = plugin.proxy.getPlayer(this.uuid).isPresent