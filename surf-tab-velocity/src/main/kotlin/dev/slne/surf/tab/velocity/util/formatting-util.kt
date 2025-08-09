package dev.slne.surf.tab.velocity.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.william278.papiproxybridge.api.PlaceholderAPI
import java.util.*
import java.util.concurrent.CompletableFuture

val papiProxyInstance = PlaceholderAPI.createInstance()

fun String.formatMiniMessage(player: UUID): CompletableFuture<Component> =
    papiProxyInstance.formatPlaceholders(this, player).thenApply {
        MiniMessage.miniMessage().deserialize(it)
    }