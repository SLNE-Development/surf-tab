package dev.slne.surf.tab.velocity.service

import dev.slne.surf.cloud.api.common.sync.SyncSet
import dev.slne.surf.cloud.api.common.sync.SyncValue
import dev.slne.surf.tab.api.entry.TabGroup
import org.springframework.stereotype.Component

@Component
class VelocitySyncService {
    val tabHeader = SyncValue("tablist:header", "Internal Server Error")
    val tabFooter = SyncValue("tablist:footer", "Internal Server Error")
    val tabNameFormat = SyncValue("tablist:name_format", "Internal Server Error (<player_name>)")
    val tabGroups = SyncSet<TabGroup>("tablist:groups")
}