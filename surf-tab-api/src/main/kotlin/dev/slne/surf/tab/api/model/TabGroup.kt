package dev.slne.surf.tab.api.model

import dev.slne.surf.tab.api.model.TabServer

interface TabGroup {
  val name: String
  val display: String

  val servers: ObjectSet<TabServer>
}
