package dev.slne.surf.tab.core.client.service

import dev.slne.surf.tab.api.placeholder.TabPlaceholder
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSets
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.Context
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

/**
 * One header or footer, together with what rendering it depends on.
 *
 * A template is analysed once and afterwards knows two things about itself:
 *
 * - which known [TabPlaceholder]s it names, which is what makes it possible to skip a render whose
 *   inputs did not move, and
 * - whether it names any tag the tablist cannot account for, which is what makes it necessary to
 *   render it for every player separately.
 *
 * A template that only names tags MiniMessage itself renders and known placeholders produces the very
 * same component for everybody. It is therefore rendered once per update and that one component is
 * handed to every player. When the values it depends on did not move either, even that render is
 * skipped and the component of the previous update is reused.
 *
 * Instances are immutable apart from the remembered render, which is only ever replaced as a whole,
 * and are safe to share between threads.
 *
 * @param source the template as it was written
 * @param placeholders the known placeholders this template names
 * @param unknownTags the tags this template names that neither MiniMessage nor the tablist knows
 * @param rendersPerPlayer whether this template has to be rendered for every player separately
 */
class TablistTemplate internal constructor(
    val source: String,
    val placeholders: Set<TabPlaceholder>,
    val unknownTags: ObjectSet<String>,
    val rendersPerPlayer: Boolean
) {

    /**
     * The most recent audience independent render, together with the values it was built from.
     *
     * Only ever replaced as a whole, so a reader sees either a complete previous render or none.
     */
    @Volatile
    private var rendered: Rendered? = null

    /**
     * Whether this template names [placeholder].
     */
    fun dependsOn(placeholder: TabPlaceholder) = placeholders.contains(placeholder)

    /**
     * Whether any placeholder this template names renders differently in [current] than it did in
     * [previous].
     */
    fun inputsChanged(previous: TablistValues, current: TablistValues): Boolean {
        return placeholders.any { current.changed(it, previous) }
    }

    /**
     * The component every player is shown for [values], calling [render] only when the previous
     * render cannot be reused.
     *
     * Only meaningful for a template that is not [rendersPerPlayer]; one that is renders differently
     * per audience and therefore has nothing to share.
     *
     * Two updates rendering at the same time each produce a component built from their own snapshot,
     * so whichever remembers its render last is the one reused afterwards. Neither is wrong: both
     * were built from a snapshot that was current when it was taken, and the one that lost has
     * already been sent to the players it was rendered for.
     */
    fun renderShared(values: TablistValues, render: () -> Component): Component {
        require(!rendersPerPlayer) { "$this renders per player and has no component to share" }

        val previous = rendered

        if (previous != null && !inputsChanged(previous.values, values)) {
            return previous.component
        }

        val component = render()
        rendered = Rendered(values, component)

        return component
    }

    override fun toString(): String {
        return "TablistTemplate(" +
                "placeholders=${placeholders.map { it.tagName }}, " +
                "unknownTags=$unknownTags, " +
                "rendersPerPlayer=$rendersPerPlayer" +
                ")"
    }

    private class Rendered(val values: TablistValues, val component: Component)

    companion object {

        /**
         * Analyses [source] and returns what rendering it depends on.
         *
         * Every tag the template names is sorted into one of three groups. Tags [resolve] knows -
         * the tablist's own placeholders and everything a plugin registered - become the dependencies
         * that decide when a new render is needed. Tags [miniMessage] resolves - colours, decorations,
         * line breaks and whatever else the instance was built with - render the same for everybody
         * and are ignored. Everything else is unknown: it may be a MiniPlaceholder that differs per
         * audience and moves without ever saying so, so a template containing one is rendered per
         * player and refreshed on a timer.
         *
         * Unknown is deliberately the fallback rather than the exception. A tag nobody resolves ends
         * up as literal text and would be safe to share, but telling that apart from a placeholder
         * some other plugin contributes is not something the tablist can do reliably, and being
         * wrong in that direction means showing stale text forever.
         *
         * A template that cannot be parsed is treated as unknown as well. Rendering it fails the
         * same way it does without any of this, whereas assuming it has no dependencies would leave
         * it quietly stale.
         */
        fun analyze(
            source: String,
            miniMessage: MiniMessage,
            resolve: (String) -> TabPlaceholder? = BuiltinPlaceholder::byTagName
        ): TablistTemplate {
            val names = tagNamesIn(source, miniMessage)
                ?: return TablistTemplate(
                    source = source,
                    placeholders = emptySet(),
                    unknownTags = ObjectSets.emptySet(),
                    rendersPerPlayer = true
                )

            val placeholders = ObjectLinkedOpenHashSet<TabPlaceholder>()
            val unknownTags = ObjectLinkedOpenHashSet<String>()

            for (name in names) {
                val placeholder = resolve(name)

                when {
                    placeholder != null -> placeholders.add(placeholder)
                    miniMessage.tags().has(name) -> Unit
                    else -> unknownTags.add(name)
                }
            }

            return TablistTemplate(source, placeholders, unknownTags, unknownTags.isNotEmpty())
        }

        /**
         * Every tag name [source] names, or `null` if it cannot be parsed.
         */
        private fun tagNamesIn(source: String, miniMessage: MiniMessage): Set<String>? {
            val recorder = TagNameRecorder()

            return try {
                miniMessage.deserializeToTree(source, recorder)
                recorder.names
            } catch (_: RuntimeException) {
                null
            }
        }
    }
}

/**
 * A resolver that resolves nothing and writes down every tag name it is offered.
 */
private class TagNameRecorder : TagResolver {
    val names = LinkedHashSet<String>()

    override fun resolve(name: String, arguments: ArgumentQueue, ctx: Context): Tag? {
        names += name
        return null
    }

    override fun has(name: String): Boolean {
        names += name
        return false
    }
}
