package io.github.pavelannin.vexillum.plugin.dsl

import io.github.pavelannin.vexillum.plugin.model.FlowSharingStarted
import java.io.Serializable

sealed interface FeatureFlagDsl : Serializable

class ImmutableFeatureFlagDsl internal constructor(val name: String) : FeatureFlagDsl {
    var id: String? = null
    var description: String? = null
    var valueType: String? = null
    var kDoc: String? = null
    var deprecated: Boolean = false
    var value: String? = null
}

class MutableFeatureFlagDsl internal constructor(val name: String) : FeatureFlagDsl {
    var id: String? = null
    var description: String? = null
    var valueType: String? = null
    var kDoc: String? = null
    var deprecated: Boolean = false
    var defaultValue: String? = null
}

class FlowFeatureFlagDsl internal constructor(val name: String) : FeatureFlagDsl {
    var id: String? = null
    var description: String? = null
    var valueType: String? = null
    var kDoc: String? = null
    var deprecated: Boolean = false
    var defaultValue: String? = null
    var startedStrategy: FlowSharingStarted = FlowSharingStarted.Lazily
}
