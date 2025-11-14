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
    private val contextualExtensions =
        CopyOnWriteArrayList<Pair<Class<*>, ContextualPlaceholderExtension<*>>>()
    private val asyncContextualExtensions =
        CopyOnWriteArrayList<Pair<Class<*>, AsyncContextualPlaceholderExtension<*>>>()
    private val mini = MiniMessage.miniMessage()

    fun register(ext: PlaceholderExtension) = syncExtensions.add(ext)
    fun registerAsync(ext: AsyncPlaceholderExtension) = asyncExtensions.add(ext)
    fun <P> registerContextual(clazz: Class<P>, ext: ContextualPlaceholderExtension<P>) =
        contextualExtensions.add(clazz to ext)

    fun <P> registerAsyncContextual(clazz: Class<P>, ext: AsyncContextualPlaceholderExtension<P>) =
        asyncContextualExtensions.add(clazz to ext)

    fun clear() {
        syncExtensions.clear()
        asyncExtensions.clear()
        contextualExtensions.clear()
        asyncContextualExtensions.clear()
    }

    private fun combine(vararg lists: List<TagResolver>): TagResolver {
        val flat = lists.flatMap { it }
        return TagResolver.resolver(*flat.toTypedArray())
    }

    private fun syncResolvers() = syncExtensions.map { it.resolver() }

    private fun <P> contextualResolvers(context: P): List<TagResolver> =
        contextualExtensions
            .filter { it.first.isInstance(context) }
            .map { (_, ext) ->
                @Suppress("UNCHECKED_CAST")
                (ext as ContextualPlaceholderExtension<P>).resolver(context)
            }

    private suspend fun asyncResolvers(): List<TagResolver> = coroutineScope {
        asyncExtensions.map { ext ->
            async(Dispatchers.IO) { ext.resolver() }
        }.awaitAll()
    }

    private suspend fun <P> asyncContextualResolvers(context: P): List<TagResolver> =
        coroutineScope {
            asyncContextualExtensions
                .filter { it.first.isInstance(context) }
                .map { (_, ext) ->
                    async(Dispatchers.IO) {
                        @Suppress("UNCHECKED_CAST")
                        (ext as AsyncContextualPlaceholderExtension<P>).resolver(context)
                    }
                }.awaitAll()
        }

    fun parse(message: String): Component {
        val sync = syncResolvers()
        return mini.deserialize(message, combine(sync))
    }

    fun <P> parse(message: String, context: P): Component {
        val sync = syncResolvers()
        val ctx = contextualResolvers(context)
        return mini.deserialize(message, combine(sync, ctx))
    }

    suspend fun parseAsync(message: String): Component {
        val sync = syncResolvers()
        val async = asyncResolvers()
        return mini.deserialize(message, combine(sync, async))
    }

    suspend fun <P> parseAsync(message: String, context: P): Component {
        val sync = syncResolvers()
        val ctx = contextualResolvers(context)
        val async = asyncResolvers()
        val asyncCtx = asyncContextualResolvers(context)
        return mini.deserialize(message, combine(sync, ctx, async, asyncCtx))
    }

    fun parseAsync(message: String, callback: (Component) -> Unit) {
        plugin.launch(Dispatchers.IO) {
            val result = parseAsync(message)
            withContext(Dispatchers.Main) { callback(result) }
        }
    }

    fun <P> parseAsync(message: String, context: P, callback: (Component) -> Unit) {
        plugin.launch(Dispatchers.IO) {
            val result = parseAsync(message, context)
            withContext(Dispatchers.Main) { callback(result) }
        }
    }
}
