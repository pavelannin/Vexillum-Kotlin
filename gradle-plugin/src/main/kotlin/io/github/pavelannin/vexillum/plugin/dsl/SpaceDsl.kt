package io.github.pavelannin.vexillum.plugin.dsl

import io.github.pavelannin.vexillum.plugin.model.SpaceStyle
import java.io.Serializable


class SpaceDsl internal constructor(val name: String) : Serializable {
    var deprecated: Boolean = false
    var kDoc: String? = null
    var style: SpaceStyle = SpaceStyle.Delegate

    internal val featureFlags = mutableListOf<FeatureFlagDsl>()

    fun immutable(name: String, init: ImmutableFeatureFlagDsl.() -> Unit) {
        val feature = ImmutableFeatureFlagDsl(name).apply(init)
        featureFlags += feature
    }

    fun mutable(name: String, init: MutableFeatureFlagDsl.() -> Unit) {
        val feature = MutableFeatureFlagDsl(name).apply(init)
        featureFlags += feature
    }

    fun flow(name: String, init: FlowFeatureFlagDsl.() -> Unit) {
        val feature = FlowFeatureFlagDsl(name).apply(init)
        featureFlags += feature
    }
}
