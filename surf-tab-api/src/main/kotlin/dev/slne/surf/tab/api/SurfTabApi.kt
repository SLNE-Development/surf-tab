package dev.slne.surf.tab.api

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.tab.api.placeholder.TabPlaceholder
import java.util.*

interface SurfTabApi {

    /**
     * Registers the specified [placeholder].
     *
     * Templates referencing the placeholder are re-rendered for all players when it is registered.
     * Subsequent updates are controlled by the placeholder's [TabPlaceholder.updates] condition.
     *
     * @param placeholder the placeholder to register
     * @throws IllegalArgumentException if the placeholder name is already registered or is not
     * lowercase
     */
    fun registerPlaceholder(placeholder: TabPlaceholder)

    /**
     * Unregisters the specified [placeholder].
     *
     * @param placeholder the placeholder to unregister
     */
    fun unregisterPlaceholder(placeholder: TabPlaceholder)

    /**
     * Invalidates the specified [placeholders], indicating that their values may have changed.
     *
     * Templates referencing at least one of the placeholders are re-rendered. Updated content is sent
     * only when the rendered result differs from the previous result.
     *
     * For placeholders using
     * [dev.slne.surf.tab.api.placeholder.UpdateCondition.OnDemand], invalidation is the only update
     * trigger. For all other update conditions, it acts as an additional trigger.
     *
     * @param placeholders the placeholders to invalidate
     */
    fun invalidate(vararg placeholders: TabPlaceholder)

    /**
     * Re-renders the tab entry of the player identified by [uuid].
     *
     * The updated entry is sent only if its visible content has changed. Plugins that modify their
     * contribution in [dev.slne.surf.tab.api.event.TabEntryRenderEvent] should call this method after
     * the relevant data changes.
     *
     * @param uuid the UUID of the player whose tab entry should be updated
     */
    fun updateEntry(uuid: UUID)

    /**
     * Requests an [updateEntry] for the player identified by [uuid] on every server in the network.
     *
     * The update request is propagated through the tab list's internal communication channel.
     *
     * @param uuid the UUID of the player whose tab entry should be updated
     */
    fun broadcastEntryUpdate(uuid: UUID)

    companion object : SurfTabApi by INSTANCE {
        val instance = INSTANCE
    }
}

private val INSTANCE = requiredService<SurfTabApi>()
