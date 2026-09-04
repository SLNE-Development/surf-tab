package dev.slne.surf.tab.core.client.service

import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.text
import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.core.api.common.server.SurfServer
import dev.slne.surf.tab.api.placeholder.TabPlaceholder
import dev.slne.surf.tab.api.placeholder.UpdateCondition
import dev.slne.surf.tab.core.client.platform.tabPlatform
import dev.slne.surf.tab.core.client.util.formatTablistDate
import dev.slne.surf.tab.core.client.util.formatTablistTime
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.kyori.adventure.text.Component
import java.time.ZonedDateTime

/**
 * The placeholders the tablist fills in itself.
 *
 * [tagName] is matched against the names MiniMessage reports while it parses a template, which are
 * always lower case.
 */
enum class BuiltinPlaceholder(
    override val tagName: String,
    override val updates: UpdateCondition
) : TabPlaceholder {

    /** The name of this server. */
    SERVER("server", UpdateCondition.Never) {
        override fun value(): Component = text(SurfServer.current().name, Colors.VARIABLE_VALUE)
    },

    /** How many players are on this server. */
    PLAYERS_ONLINE("players_online", UpdateCondition.OnDemand) {
        override fun value(): Component = text(tabPlatform.onlinePlayerCount(), Colors.INFO)
    },

    /** How many players this server has room for. */
    PLAYERS_MAX("players_max", UpdateCondition.OnDemand) {
        override fun value(): Component = text(tabPlatform.maxPlayerCount(), Colors.INFO)
    },

    /** Today's date. */
    DATE("date", UpdateCondition.OnDemand) {
        override fun value(): Component = text(formatTablistDate(ZonedDateTime.now()), Colors.INFO)
    },

    /** The time of day without seconds. */
    TIME("time", UpdateCondition.OnDemand) {
        override fun value(): Component = text(formatTablistTime(ZonedDateTime.now()), Colors.INFO)
    };

    companion object {
        private val byName = entries
            .associateByTo(Object2ObjectOpenHashMap(entries.size)) { it.tagName }
            .freeze()

        /**
         * The placeholder written as `<`[tagName]`>`, or `null` if the tablist does not fill that tag
         * in itself.
         */
        fun byTagName(tagName: String): BuiltinPlaceholder? = byName[tagName]
    }
}
