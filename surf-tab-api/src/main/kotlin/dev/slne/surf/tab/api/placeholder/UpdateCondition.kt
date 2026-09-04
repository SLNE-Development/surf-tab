package dev.slne.surf.tab.api.placeholder

import dev.slne.surf.api.core.event.SurfEvent
import kotlin.time.Duration

/**
 * Defines when the value of a [TabPlaceholder] may have changed.
 *
 * The tab list evaluates a placeholder only when its update condition is triggered. The newly
 * resolved value is compared with the previously captured value, and dependent templates are
 * re-rendered only when the value actually changed.
 *
 * Multiple conditions can be combined using [plus].
 */
sealed interface UpdateCondition {

    /**
     * Indicates that the placeholder value never changes after its initial evaluation.
     */
    data object Never : UpdateCondition

    /**
     * Indicates that the placeholder value changes only when explicitly invalidated through
     * [dev.slne.surf.tab.api.SurfTabApi.invalidate].
     */
    data object OnDemand : UpdateCondition

    /**
     * Re-evaluates the placeholder periodically after each [interval].
     *
     * @property interval the interval between evaluations
     * @throws IllegalArgumentException if [interval] is not positive
     */
    data class Every(val interval: Duration) : UpdateCondition {
        init {
            require(interval.isPositive()) { "interval must be positive but was $interval" }
        }
    }

    /**
     * Re-evaluates the placeholder when an event assignable to [type] is dispatched on the Surf
     * event bus and satisfies [matches].
     *
     * @param E the event type observed by this condition
     * @property type the event class to observe
     * @property matches determines whether a received event should trigger an update
     */
    class OnEvent<E : SurfEvent>(
        val type: Class<E>,
        val matches: (E) -> Boolean = { true }
    ) : UpdateCondition {
        override fun toString() = "OnEvent(${type.simpleName})"
    }

    /**
     * Combines multiple update [conditions].
     *
     * The placeholder is considered invalid whenever any contained condition is triggered.
     *
     * @property conditions the conditions that can trigger an update
     */
    data class AnyOf(val conditions: List<UpdateCondition>) : UpdateCondition

    /**
     * Combines this condition with [other].
     *
     * Nested [AnyOf] conditions are flattened in the resulting condition.
     *
     * @param other the condition to combine with this one
     * @return a condition triggered whenever either condition is triggered
     */
    operator fun plus(other: UpdateCondition): UpdateCondition = AnyOf(flatten(this) + flatten(other))

    companion object {

        /**
         * Creates an [OnEvent] condition for events of type [E].
         *
         * @param E the event type to observe
         * @param matches determines whether a received event should trigger an update
         * @return an event-based update condition
         */
        inline fun <reified E : SurfEvent> onEvent(
            noinline matches: (E) -> Boolean = { true }
        ) = OnEvent(E::class.java, matches)

        /**
         * Returns all leaf conditions contained in [condition].
         *
         * Nested [AnyOf] instances are recursively unwrapped while all other condition types are
         * returned unchanged.
         *
         * @param condition the condition to flatten
         * @return the flattened leaf conditions
         */
        fun flatten(condition: UpdateCondition): List<UpdateCondition> = when (condition) {
            is AnyOf -> condition.conditions.flatMap(::flatten)
            else -> listOf(condition)
        }
    }
}
