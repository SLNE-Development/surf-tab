package dev.slne.surf.tab.core.model

import dev.slne.surf.tab.api.TabEntry
import dev.slne.surf.tab.api.player.TabGameMode
import net.kyori.adventure.text.Component
import java.util.*

data class TabEntryImpl(
    override val associatedPlayer: UUID,
    override val associatedName: String,
    override val display: Component,
    override val gameMode: TabGameMode,
    override val ping: Int,
    override val weight: Int
) : TabEntry