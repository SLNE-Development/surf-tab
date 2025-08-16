package dev.slne.surf.tab.api.model

interface TabServer {
  val name: String
  fun retrievePlayers(): ObjectSet<TabPlayer>
}
