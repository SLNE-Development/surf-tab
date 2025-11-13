package dev.slne.surf.tab.server.placeholder.type

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

interface PlaceholderExtension {
    val name: String
    fun resolver(): TagResolver
}
