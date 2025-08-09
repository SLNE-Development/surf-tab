package dev.slne.surf.tab.core.entity

import dev.slne.surf.tab.api.player.TabPlayer
import java.util.UUID

data class TabPlayerImpl(override val name: String, override val uniqueId: UUID) : TabPlayer