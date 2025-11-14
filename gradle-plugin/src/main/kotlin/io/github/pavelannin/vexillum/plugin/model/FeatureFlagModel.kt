package io.github.pavelannin.vexillum.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable

@Serializable
internal sealed interface FeatureFlagModel : JavaSerializable {
    val name: String
    val id: String
    val description: String?
    val valueType: String
    val kDoc: String?
    val deprecated: Boolean
}

@SerialName("immutable")
@Serializable
data class ImmutableFeatureFlagModel(
    override val name: String,
    override val id: String,
    override val valueType: String,
    override val description: String? = null,
    override val kDoc: String? = null,
    override val deprecated: Boolean = false,
    val value: String,
) : FeatureFlagModel

@SerialName("mutable")
@Serializable
data class MutableFeatureFlagModel(
    override val name: String,
    override val id: String,
    override val valueType: String,
    override val description: String? = null,
    override val kDoc: String? = null,
    override val deprecated: Boolean = false,
    val defaultValue: String,
) : FeatureFlagModel

@SerialName("flow")
@Serializable
data class FlowFeatureFlagModel(
    override val name: String,
    override val id: String,
    override val valueType: String,
    override val description: String? = null,
    override val kDoc: String? = null,
    override val deprecated: Boolean = false,
    val defaultValue: String,
    val startedStrategy: FlowSharingStarted = FlowSharingStarted.Lazily
) : FeatureFlagModel

@Serializable
enum class FlowSharingStarted {
    @SerialName("eagerly") Eagerly,
    @SerialName("lazily") Lazily
}