package dev.slne.surf.tab.core.client.platform

import net.kyori.adventure.audience.Audience
import java.util.*

/**
 * Represents a player who can receive tab list header and footer content.
 */
interface TabViewer {

    /**
     * The unique identifier of this viewer.
     */
    val uuid: UUID

    /**
     * The Adventure [Audience] used to send tab list content to this viewer and resolve
     * audience-specific placeholders.
     */
    val audience: Audience
}
