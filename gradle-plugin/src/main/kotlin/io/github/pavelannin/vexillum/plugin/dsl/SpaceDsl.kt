package io.github.pavelannin.vexillum.plugin.dsl

import java.io.Serializable

class SpaceDsl internal constructor(val name: String) : Serializable {
    var isDeprecated: Boolean = false
    var kDoc: String? = null
    var isDelegateStyle: Boolean = true

    internal val featureFlags = mutableListOf<FeatureFlagDsl>()

    fun immutable(name: String, init: FeatureFlagDsl.Immutable.() -> Unit) {
        val feature = FeatureFlagDsl.Immutable(name).apply(init)
        featureFlags += feature
    }

    fun mutable(name: String, init: FeatureFlagDsl.Mutable.() -> Unit) {
        val feature = FeatureFlagDsl.Mutable(name).apply(init)
        featureFlags += feature
    }

    fun flow(name: String, init: FeatureFlagDsl.Flow.() -> Unit) {
        val feature = FeatureFlagDsl.Flow(name).apply(init)
        featureFlags += feature
    }
}

sealed interface FeatureFlagDsl : Serializable {
    class Immutable internal constructor(val name: String) : FeatureFlagDsl {
        var id: String? = null
        var description: String? = null
        var value: String? = null
        var valueType: String? = null
        var kDoc: String? = null
        var isDeprecated: Boolean = false
    }

    class Mutable internal constructor(val name: String) : FeatureFlagDsl {
        var id: String? = null
        var description: String? = null
        var defaultValue: String? = null
        var valueType: String? = null
        var kDoc: String? = null
        var isDeprecated: Boolean = false
    }

    class Flow internal constructor(val name: String) : FeatureFlagDsl {
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
