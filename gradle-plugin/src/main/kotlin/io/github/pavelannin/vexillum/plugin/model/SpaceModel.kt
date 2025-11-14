package io.github.pavelannin.vexillum.plugin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable

@Serializable
internal data class SpaceModel(
    val name: String,
    val flags: List<FeatureFlagModel> = emptyList(),
    val kDoc: String? = null,
    val deprecated: Boolean = false,
    val style: SpaceStyle = SpaceStyle.Delegate,
) : JavaSerializable

@Serializable
enum class SpaceStyle {
    @SerialName("property") Property,
    @SerialName("delegate") Delegate,
}
