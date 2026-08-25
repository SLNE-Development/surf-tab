package dev.slne.surf.tab.core.client.service

/**
 * Everything the tablist fills its own placeholders with, read once for a whole update.
 *
 * @param generation counts snapshots up, so that a later snapshot is recognisable as the newer one
 * @param server the name of this server
 * @param onlinePlayers how many players were on this server when the snapshot was taken
 * @param maxPlayers how many players this server had room for when the snapshot was taken
 * @param date the day the snapshot was taken, already written the way the tablist spells it
 * @param time the time the snapshot was taken, already written the way the tablist spells it
 */
class TablistValues(
    val generation: Long,
    val server: String,
    val onlinePlayers: Int,
    val maxPlayers: Int,
    val date: String,
    val time: String
) {

    override fun toString(): String {
        return "TablistValues(" +
                "generation=$generation, " +
                "server='$server', " +
                "onlinePlayers=$onlinePlayers, " +
                "maxPlayers=$maxPlayers, " +
                "date='$date', " +
                "time='$time'" +
                ")"
    }
}
