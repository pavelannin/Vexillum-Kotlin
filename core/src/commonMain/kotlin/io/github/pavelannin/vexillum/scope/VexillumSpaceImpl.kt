package io.github.pavelannin.vexillum.scope

import io.github.pavelannin.vexillum.FeatureFlagSpec
import io.github.pavelannin.vexillum.FlowFeatureFlagSpec
import io.github.pavelannin.vexillum.ImmutableFeatureFlagSpec
import io.github.pavelannin.vexillum.MutableFeatureFlagSpec
import io.github.pavelannin.vexillum.Vexillum
import kotlinx.coroutines.flow.SharingStarted
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

public fun VexillumSpace(
    vexillum: Vexillum,
): VexillumSpace = VexillumSpaceImpl(vexillum)

private class VexillumSpaceImpl(
    private val vexillum: Vexillum,
) : VexillumSpace {
    private val specs = mutableSetOf<FeatureFlagSpec<*>>()

    override fun <Value : Any> immutable(
        id: String,
        value: Value,
        valueType: KClass<Value>,
        description: String?,
    ): ReadOnlyProperty<VexillumSpace, ImmutableFeatureFlagValue<Value>> {
        val spec = ImmutableFeatureFlagSpec(id, value, valueType, description)
        return ReadOnlyProperty { _, _ -> ImmutableFeatureFlagValue(spec, vexillum[spec]) }
    }

    override fun <Value : Any> mutable(
        id: String,
        defaultValue: Value,
        valueType: KClass<Value>,
        description: String?,
    ): ReadOnlyProperty<VexillumSpace, MutableFeatureFlagValue<Value>> {
        val spec = MutableFeatureFlagSpec(id, defaultValue, valueType, description)
        return ReadOnlyProperty { _, _ -> MutableFeatureFlagValue(spec) { vexillum[spec] } }
    }

    override fun <Value : Any> flow(
        id: String,
        defaultValue: Value,
        valueType: KClass<Value>,
        description: String?,
        startedStrategy: SharingStarted,
    ): ReadOnlyProperty<VexillumSpace, FlowFeatureFlagValue<Value>> {
        val spec = FlowFeatureFlagSpec(id, defaultValue, valueType, description, startedStrategy)
        return ReadOnlyProperty { _, _ -> FlowFeatureFlagValue(spec, vexillum[spec]) }
    }

    override fun allSpecs(): Set<FeatureFlagSpec<*>> {
        return specs.toSet()
    }
}
