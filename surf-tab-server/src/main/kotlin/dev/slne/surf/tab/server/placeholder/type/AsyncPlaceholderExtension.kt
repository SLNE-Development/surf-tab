package dev.slne.surf.tab.server.placeholder.type

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

interface AsyncPlaceholderExtension {
    val name: String
    suspend fun resolver(): TagResolver
}
