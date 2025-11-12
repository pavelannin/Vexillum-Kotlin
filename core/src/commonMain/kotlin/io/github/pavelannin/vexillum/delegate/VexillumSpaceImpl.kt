package io.github.pavelannin.vexillum.delegate

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
    id: String,
    description: String? = null,
): VexillumSpace = VexillumSpaceImpl(id, description, vexillum)

private class VexillumSpaceImpl(
    override val id: String,
    override val description: String?,
    private val vexillum: Vexillum,
) : VexillumSpace {
    private val specs = mutableSetOf<FeatureFlagSpec<*>>()

    override fun <Value : Any> immutable(
        id: String,
        value: Value,
        valueType: KClass<Value>,
        description: String?
    ): ReadOnlyProperty<VexillumSpaceImpl, ImmutableFeatureFlagValue<Value>> {
        val spec = ImmutableFeatureFlagSpec(id, value, valueType, description)
        return ReadOnlyProperty { ref, _ -> ImmutableFeatureFlagValue(spec, ref.vexillum[spec]) }
    }

    override fun <Value : Any> mutable(
        id: String,
        defaultValue: Value,
        valueType: KClass<Value>,
        description: String?
    ): ReadOnlyProperty<VexillumSpaceImpl, MutableFeatureFlagValue<Value>> {
        val spec = MutableFeatureFlagSpec(id, defaultValue, valueType, description)
        return ReadOnlyProperty { ref, _ -> MutableFeatureFlagValue(spec) { ref.vexillum[spec] } }
    }

    override fun <Value : Any> flow(
        id: String,
        defaultValue: Value,
        valueType: KClass<Value>,
        description: String?,
        startedStrategy: SharingStarted
    ): ReadOnlyProperty<VexillumSpaceImpl, FlowFeatureFlagValue<Value>> {
        val spec = FlowFeatureFlagSpec(id, defaultValue, valueType, description, startedStrategy)
        return ReadOnlyProperty { ref, _ -> FlowFeatureFlagValue(spec, ref.vexillum[spec]) }
    }

    override fun allSpecs(): Set<FeatureFlagSpec<*>> {
        return specs.toSet()
    }
}
