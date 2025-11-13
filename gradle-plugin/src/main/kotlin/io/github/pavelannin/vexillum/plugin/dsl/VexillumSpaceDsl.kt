package io.github.pavelannin.vexillum.plugin.dsl

import java.io.Serializable

class VexillumSpaceDsl internal constructor(val name: String) : Serializable {
    var isDeprecated: Boolean = false
    var kDoc: String? = null
    var isDelegateStyle: Boolean = true

    internal val featureFlags = mutableListOf<VexillumFeatureFlagDsl>()

    fun immutable(name: String, init: VexillumFeatureFlagDsl.Immutable.() -> Unit) {
        val feature = VexillumFeatureFlagDsl.Immutable(name).apply(init)
        featureFlags += feature
    }

    fun mutable(name: String, init: VexillumFeatureFlagDsl.Mutable.() -> Unit) {
        val feature = VexillumFeatureFlagDsl.Mutable(name).apply(init)
        featureFlags += feature
    }

    fun flow(name: String, init: VexillumFeatureFlagDsl.Flow.() -> Unit) {
        val feature = VexillumFeatureFlagDsl.Flow(name).apply(init)
        featureFlags += feature
    }
}

sealed interface VexillumFeatureFlagDsl : Serializable {
    class Immutable internal constructor(val name: String) : VexillumFeatureFlagDsl {
        var id: String? = null
        var description: String? = null
        var value: String? = null
        var valueType: String? = null
        var kDoc: String? = null
        var isDeprecated: Boolean = false
    }

    class Mutable internal constructor(val name: String) : VexillumFeatureFlagDsl {
        var id: String? = null
        var description: String? = null
        var defaultValue: String? = null
        var valueType: String? = null
        var kDoc: String? = null
        var isDeprecated: Boolean = false
    }

    class Flow internal constructor(val name: String) : VexillumFeatureFlagDsl {
        var id: String? = null
        var description: String? = null
        var defaultValue: String? = null
        var valueType: String? = null
        var startedStrategy: SharingStarted = SharingStarted.Lazily
        var kDoc: String? = null
        var isDeprecated: Boolean = false

        enum class SharingStarted { Eagerly, Lazily }
    }
}
