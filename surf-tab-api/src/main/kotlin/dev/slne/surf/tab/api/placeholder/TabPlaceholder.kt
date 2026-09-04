package dev.slne.surf.tab.api.placeholder

import net.kyori.adventure.text.Component

/**
 * Represents a value that can be referenced from tab list header and footer templates.
 *
 * Registered placeholders are known to the tab list and can therefore be updated selectively.
 * Templates referencing this placeholder are re-rendered only when [updates] indicates that its value
 * may have changed. The newly resolved value is compared with the previously captured value so
 * unchanged placeholders do not cause unnecessary output updates.
 *
 * This differs from unknown placeholders, whose changes cannot be observed directly and therefore
 * require periodic per-player rendering.
 */
interface TabPlaceholder {

    /**
     * The tag name used to reference this placeholder from a template, without angle brackets.
     *
     * Tag names must be lowercase and unique among all registered placeholders. Names reserved by
     * built-in placeholders cannot be registered.
     */
    val tagName: String

    /**
     * Defines when this placeholder's value may have changed and should be evaluated again.
     */
    val updates: UpdateCondition

    /**
     * Resolves the current value of this placeholder.
     *
     * The returned component is compared with the previously captured value to determine whether
     * templates referencing this placeholder need to produce an update.
     *
     * @return the current placeholder value
     */
    fun value(): Component
}

/**
 * Creates a [TabPlaceholder] backed by the supplied [value] provider.
 *
 * @param tagName the lowercase tag name used to reference the placeholder from templates
 * @param updates the condition describing when the placeholder value may change
 * @param value resolves the current placeholder value
 * @return a placeholder using the supplied configuration and value provider
 */
fun tabPlaceholder(
    tagName: String,
    updates: UpdateCondition,
    value: () -> Component
): TabPlaceholder = object : TabPlaceholder {
    override val tagName = tagName
    override val updates = updates
    override fun value() = value()
    override fun toString() = "TabPlaceholder(<$tagName>, $updates)"
}
