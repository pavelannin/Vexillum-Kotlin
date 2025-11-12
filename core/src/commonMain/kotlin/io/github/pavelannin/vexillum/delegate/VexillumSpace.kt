package io.github.pavelannin.vexillum.delegate

import io.github.pavelannin.vexillum.FeatureFlagSpec
import kotlinx.coroutines.flow.SharingStarted
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

public interface VexillumSpace {
    public val id: String
    public val description: String?

    public fun <Value : Any> immutable(
        id: String,
        value: Value,
        valueType: KClass<Value>,
        description: String? = null,
    ): ReadOnlyProperty<*, ImmutableFeatureFlagValue<Value>>

    public fun <Value : Any> mutable(
        id: String,
        defaultValue: Value,
        valueType: KClass<Value>,
        description: String? = null,
    ): ReadOnlyProperty<*, MutableFeatureFlagValue<Value>>

    public fun <Value : Any> flow(
        id: String,
        defaultValue: Value,
        valueType: KClass<Value>,
        description: String? = null,
        startedStrategy: SharingStarted = SharingStarted.Lazily,
    ): ReadOnlyProperty<*, FlowFeatureFlagValue<Value>>

    public fun allSpecs(): Set<FeatureFlagSpec<*>>
}

public inline fun <reified Value : Any> VexillumSpace.immutable(
    id: String,
    value: Value,
    description: String? = null,
): ReadOnlyProperty<*, ImmutableFeatureFlagValue<Value>> {
    return immutable(id, value, Value::class, description)
}

public inline fun <reified Value : Any> VexillumSpace.mutable(
    id: String,
    defaultValue: Value,
    description: String? = null,
): ReadOnlyProperty<*, MutableFeatureFlagValue<Value>> {
    return mutable(id, defaultValue, Value::class, description)
}

public inline fun <reified Value : Any> VexillumSpace.flow(
    id: String,
    defaultValue: Value,
    description: String? = null,
    startedStrategy: SharingStarted = SharingStarted.Lazily,
): ReadOnlyProperty<*, FlowFeatureFlagValue<Value>> {
    return flow(id, defaultValue, Value::class, description, startedStrategy)
}
