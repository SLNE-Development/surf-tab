package dev.slne.surf.tab.server.placeholder

import dev.slne.surf.tab.server.placeholder.type.AsyncContextualPlaceholderExtension
import dev.slne.surf.tab.server.placeholder.type.AsyncPlaceholderExtension
import dev.slne.surf.tab.server.placeholder.type.ContextualPlaceholderExtension
import dev.slne.surf.tab.server.placeholder.type.PlaceholderExtension
import dev.slne.surf.tab.server.plugin
import kotlinx.coroutines.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Credits to ChatGPT
 */
object PlaceholderManager {
    private val syncExtensions = CopyOnWriteArrayList<PlaceholderExtension>()
    private val asyncExtensions = CopyOnWriteArrayList<AsyncPlaceholderExtension>()

    private val miniMessage = MiniMessage.miniMessage()

    fun register(extension: PlaceholderExtension) = syncExtensions.add(extension)
    fun registerAsync(extension: AsyncPlaceholderExtension) = asyncExtensions.add(extension)
    fun clear() {
        syncExtensions.clear()
        asyncExtensions.clear()
    }

    private fun combineResolvers(vararg groups: List<TagResolver>): TagResolver {
        val flattened = groups.asIterable().flatten()
        return TagResolver.resolver(*flattened.toTypedArray())
    }

    fun parse(message: String): Component {
        val syncResolvers = syncExtensions.map { it.resolver() }
        return miniMessage.deserialize(message, combineResolvers(syncResolvers))
    }

    suspend fun parseAsync(message: String): Component {
        val syncResolvers = syncExtensions.map { it.resolver() }

        val asyncResolvers: List<TagResolver> = coroutineScope {
            val deferred = asyncExtensions.map { extension ->
                async<TagResolver>(Dispatchers.IO) { extension.resolver() }
            }
            deferred.awaitAll()
        }

        return miniMessage.deserialize(message, combineResolvers(syncResolvers, asyncResolvers))
    }

    fun parseAsync(message: String, callback: (Component) -> Unit) {
        plugin.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { callback(parseAsync(message)) }
        }
    }

    fun <P> parseWith(
        message: String,
        context: P,
        extensions: List<ContextualPlaceholderExtension<P>>
    ): Component {
        val resolvers = extensions.map { it.resolver(context) }
        return miniMessage.deserialize(message, TagResolver.resolver(resolvers))
    }

    suspend fun <P> parseWithAsync(
        message: String,
        context: P,
        extensions: List<AsyncContextualPlaceholderExtension<P>>
    ): Component {
        val resolvers = coroutineScope {
            extensions.map { async { it.resolver(context) } }.awaitAll()
        }
        return miniMessage.deserialize(message, TagResolver.resolver(resolvers))
    }

    fun <P> parseWithAsync(
        message: String,
        context: P,
        extensions: List<AsyncContextualPlaceholderExtension<P>>,
        callback: (Component) -> Unit
    ) {
        plugin.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { callback(parseWithAsync(message, context, extensions)) }
        }
    }
}

