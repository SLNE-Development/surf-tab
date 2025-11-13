package dev.slne.surf.tab.server.placeholder.type

import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

interface ContextualPlaceholderExtension<P> {
    val name: String
    fun resolver(context: P): TagResolver
}