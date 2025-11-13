package dev.slne.surf.tab.server.placeholder.impl

import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.tab.server.placeholder.type.AsyncContextualPlaceholderExtension
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

object LuckPermsPlaceholderExtension : AsyncContextualPlaceholderExtension<CloudPlayer> {
    override val name = "luckperms"

    override suspend fun resolver(context: CloudPlayer) = TagResolver.resolver(
        Placeholder.parsed("prefix", context.getLuckpermsMetaData("prefix") ?: ""),
        Placeholder.parsed("suffix", context.getLuckpermsMetaData("suffix") ?: "")
    )
}