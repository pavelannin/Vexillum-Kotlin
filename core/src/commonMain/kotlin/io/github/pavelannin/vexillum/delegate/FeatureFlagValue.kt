package io.github.pavelannin.vexillum.delegate

import io.github.pavelannin.vexillum.FeatureFlagSpec
import io.github.pavelannin.vexillum.FlowFeatureFlagSpec
import io.github.pavelannin.vexillum.ImmutableFeatureFlagSpec
import io.github.pavelannin.vexillum.MutableFeatureFlagSpec
import io.github.pavelannin.vexillum.delegate.annotation.FlowFeatureFlagMomentValue
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.StateFlow

public sealed interface FeatureFlagValue<Value : Any> {
    public val spec: FeatureFlagSpec<Value>
}

public data class ImmutableFeatureFlagValue<Value : Any>(
    override val spec: ImmutableFeatureFlagSpec<Value>,
    public val value: Value,
) : FeatureFlagValue<Value>

public data class MutableFeatureFlagValue<Value : Any>(
    override val spec: MutableFeatureFlagSpec<Value>,
    public val value: suspend () -> Value,
) : FeatureFlagValue<Value>, suspend () -> Value by value

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
public data class FlowFeatureFlagValue<Value : Any>(
    override val spec: FlowFeatureFlagSpec<Value>,
    public val flow: StateFlow<Value>,
) : FeatureFlagValue<Value>, StateFlow<Value> by flow {
    @FlowFeatureFlagMomentValue
    override val value: Value get() = flow.value
}
