package dev.slne.surf.tab.core.client.service

/**
 * Why the header and the footer are being asked to update.
 */
enum class TablistUpdateReason {

    /** Somebody joined or left, so the number of players online moved. */
    PLAYER_COUNT,

    /** The wall clock moved on to the next minute, and with it possibly to the next day. */
    CLOCK,

    /**
     * Placeholders the tablist cannot observe may have changed.
     */
    UNKNOWN_PLACEHOLDERS,

    /** The configuration was reloaded, so the templates themselves may be different. */
    CONFIGURATION
}
