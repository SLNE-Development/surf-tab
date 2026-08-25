package dev.slne.surf.tab.core.client.service

import dev.slne.surf.api.core.util.freeze
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

/**
 * The placeholders the tablist fills in itself, as opposed to the ones MiniPlaceholders contributes.
 *
 * [tagName] is matched against the names MiniMessage reports while it parses a template, which are
 * always lower case.
 */
enum class TablistPlaceholder(val tagName: String) {

    /** The name of this server. */
    SERVER("server") {
        override fun read(values: TablistValues) = values.server
    },

    /** How many players are on this server. */
    PLAYERS_ONLINE("players_online") {
        override fun read(values: TablistValues) = values.onlinePlayers
    },

    /** How many players this server has room for. */
    PLAYERS_MAX("players_max") {
        override fun read(values: TablistValues) = values.maxPlayers
    },

    /** Today's date. */
    DATE("date") {
        override fun read(values: TablistValues) = values.date
    },

    /** The time of day without seconds. */
    TIME("time") {
        override fun read(values: TablistValues) = values.time
    };

    /**
     * The value this placeholder renders as in [values].
     */
    abstract fun read(values: TablistValues): Any

    /**
     * Whether this placeholder renders differently in [current] than it did in [previous].
     */
    fun changed(previous: TablistValues, current: TablistValues): Boolean {
        return read(previous) != read(current)
    }

    companion object {
        private val byName = entries
            .associateByTo(Object2ObjectOpenHashMap(entries.size)) {
                it.tagName
            }.freeze()

        /**
         * The placeholder written as `<`[tagName]`>`, or `null` if the tablist does not fill that tag
         * in itself.
         */
        fun byTagName(tagName: String) = byName[tagName]
    }
}
