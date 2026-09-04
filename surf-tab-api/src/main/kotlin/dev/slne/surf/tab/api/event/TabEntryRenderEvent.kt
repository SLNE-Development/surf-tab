package dev.slne.surf.tab.api.event

import dev.slne.surf.api.core.event.SurfAsyncEvent
import dev.slne.surf.api.core.event.SurfCancellableEvent
import net.kyori.adventure.text.Component
import java.util.*

/**
 * Fired before the tab list entry for the player identified by [playerUuid] is shown.
 *
 * [name] and [order] are initialized with the values the tab list would normally use: the player's
 * display name and permission weight. Event listeners may modify either value before the entry is
 * rendered. Listeners may suspend while handling this event.
 *
 * Unless [decorate] is set to `false`, the resulting [name] is subsequently decorated with additional
 * information known to the tab list.
 *
 * Cancelling the event prevents the tab list from updating the player's entry. This allows plugins
 * that manage entries themselves, such as implementations with per-viewer entries or entries for
 * players on other servers, to prevent their state from being overwritten.
 *
 * @property playerUuid the UUID of the player whose tab list entry is being rendered
 * @property name the component used as the player's displayed tab list name
 * @property order the ordering value used to position the player's entry
 */
class TabEntryRenderEvent(
    val playerUuid: UUID,
    var name: Component,
    var order: Int
) : SurfAsyncEvent(), SurfCancellableEvent {

    override var isCancelled: Boolean = false

    /**
     * Whether the tab list should apply its built-in decorations to [name].
     *
     * When enabled, decorations may be added after
     * this event has been handled. Set this to `false` to use [name] exactly as provided by listeners.
     */
    var decorate: Boolean = true
}
