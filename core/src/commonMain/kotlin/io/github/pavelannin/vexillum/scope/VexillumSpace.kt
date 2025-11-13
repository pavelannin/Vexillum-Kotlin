package io.github.pavelannin.vexillum.scope

import io.github.pavelannin.vexillum.FeatureFlagSpec
import kotlinx.coroutines.flow.SharingStarted
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

public interface VexillumSpace {
    public fun <Value : Any> immutable(
        id: String,
        value: Value,
        valueType: KClass<Value>,
        description: String? = null,
    ): ReadOnlyProperty<VexillumSpace, ImmutableFeatureFlagValue<Value>>

    public fun <Value : Any> mutable(
        id: String,
        defaultValue: Value,
        valueType: KClass<Value>,
        description: String? = null,
    ): ReadOnlyProperty<VexillumSpace, MutableFeatureFlagValue<Value>>

    public fun <Value : Any> flow(
        id: String,
        defaultValue: Value,
        valueType: KClass<Value>,
        description: String? = null,
        startedStrategy: SharingStarted = SharingStarted.Lazily,
    ): ReadOnlyProperty<VexillumSpace, FlowFeatureFlagValue<Value>>

    public fun allSpecs(): Set<FeatureFlagSpec<*>>
}

public inline fun <reified Value : Any> VexillumSpace.immutable(
    id: String,
    value: Value,
    description: String? = null,
): ReadOnlyProperty<VexillumSpace, ImmutableFeatureFlagValue<Value>> {
    return immutable(id, value, Value::class, description)
}

public inline fun <reified Value : Any> VexillumSpace.mutable(
    id: String,
    defaultValue: Value,
    description: String? = null,
): ReadOnlyProperty<VexillumSpace, MutableFeatureFlagValue<Value>> {
    return mutable(id, defaultValue, Value::class, description)
}

public inline fun <reified Value : Any> VexillumSpace.flow(
    id: String,
    defaultValue: Value,
    description: String? = null,
    startedStrategy: SharingStarted = SharingStarted.Lazily,
): ReadOnlyProperty<VexillumSpace, FlowFeatureFlagValue<Value>> {
    return flow(id, defaultValue, Value::class, description, startedStrategy)
}
