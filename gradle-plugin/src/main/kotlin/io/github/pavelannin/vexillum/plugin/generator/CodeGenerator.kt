package io.github.pavelannin.vexillum.plugin.generator

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import io.github.pavelannin.vexillum.ImmutableFeatureFlagSpec
import io.github.pavelannin.vexillum.FlowFeatureFlagSpec
import io.github.pavelannin.vexillum.MutableFeatureFlagSpec
import io.github.pavelannin.vexillum.Vexillum
import io.github.pavelannin.vexillum.scope.FlowFeatureFlagValue
import io.github.pavelannin.vexillum.scope.ImmutableFeatureFlagValue
import io.github.pavelannin.vexillum.scope.MutableFeatureFlagValue
import io.github.pavelannin.vexillum.scope.VexillumSpace
import io.github.pavelannin.vexillum.plugin.model.FlowFeatureFlagModel
import io.github.pavelannin.vexillum.plugin.model.FlowSharingStarted
import io.github.pavelannin.vexillum.plugin.model.ImmutableFeatureFlagModel
import io.github.pavelannin.vexillum.plugin.model.MutableFeatureFlagModel
import io.github.pavelannin.vexillum.plugin.model.SpaceModel
import io.github.pavelannin.vexillum.plugin.model.SpaceStyle
import kotlinx.coroutines.flow.SharingStarted
import org.gradle.internal.extensions.stdlib.capitalized

internal object CodeGenerator {
    private const val SINGLETON_NAME = "VexillumService"

    fun genSingletonFileSpec(packageClass: String): FileSpec {
        val singletonTypeSpec = TypeSpec.objectBuilder(SINGLETON_NAME)
            .addModifiers(KModifier.PUBLIC)
            .addSuperinterface(Vexillum::class, CodeBlock.of("Vexillum()"))
            .build()

        return FileSpec.builder(packageClass, SINGLETON_NAME)
            .addType(singletonTypeSpec)
            .build()
    }

    fun genSpaceFileSpec(space: SpaceModel, packageClass: String): FileSpec {
        val fileName = space.name.capitalized()

        val spaceTypeSpec = TypeSpec.objectBuilder(fileName)
            .addModifiers(KModifier.PUBLIC)

        if (space.deprecated) {
            val deprecatedAnnotationSpec =  AnnotationSpec.builder(Deprecated::class)
                .addMember("%S", "This space is deprecated.")
                .build()
            spaceTypeSpec.addAnnotation(deprecatedAnnotationSpec)
        }

        val kDoc = space.kDoc
        if (kDoc != null) {
            spaceTypeSpec.addKdoc(kDoc)
        }

        when (space.style) {
            SpaceStyle.Property -> {}
            SpaceStyle.Delegate ->
                spaceTypeSpec.addSuperinterface(VexillumSpace::class, CodeBlock.of("VexillumSpace($SINGLETON_NAME)"))
        }

        for (flag in space.flags) {
            val property = when (flag) {
                is ImmutableFeatureFlagModel -> immutablePropertySpec(flag, space.style)
                is MutableFeatureFlagModel -> mutablePropertySpec(flag, space.style)
                is FlowFeatureFlagModel -> flowPropertySpec(flag, space.style)
            }
            spaceTypeSpec.addProperty(property)
        }

        return FileSpec.builder(packageClass, fileName)
            .addType(spaceTypeSpec.build())
            .build()
    }

    private fun immutablePropertySpec(flag: ImmutableFeatureFlagModel, style: SpaceStyle): PropertySpec {
        val propertyType = when (style) {
            SpaceStyle.Property -> ImmutableFeatureFlagSpec::class
            SpaceStyle.Delegate -> ImmutableFeatureFlagValue::class
        }
            .asClassName()
            .parameterizedBy(TypeVariableName(flag.valueType))

        val propertySpec = PropertySpec.builder(flag.name, propertyType, KModifier.PUBLIC)
            .mutable(false)

        if (flag.deprecated) {
            val deprecatedAnnotationSpec =  AnnotationSpec.builder(Deprecated::class)
                .addMember("%S", "This feature flag is deprecated.")
                .build()
            propertySpec.addAnnotation(deprecatedAnnotationSpec)
        }

        val kDoc = flag.kDoc ?: flag.description
        if (kDoc != null) {
            propertySpec.addKdoc(kDoc)
        }

        when (style) {
            SpaceStyle.Property ->
                propertySpec.initializer(
                    """
                    ImmutableFeatureFlagSpec(
                        id = %S,
                        value = %L,
                        valueType = %L::class,
                        description = %S,
                    )
                """.trimIndent(),
                    flag.id, flag.value, flag.valueType, flag.description,
                )

            SpaceStyle.Delegate ->
                propertySpec.delegate(
                    """
                    immutable(
                        id = %S,
                        value = %L,
                        valueType = %L::class,
                        description = %S,
                    )
                """.trimIndent(),
                    flag.id, flag.value, flag.valueType, flag.description,
                )
        }

        return propertySpec.build()
    }

    private fun mutablePropertySpec(flag: MutableFeatureFlagModel, style: SpaceStyle): PropertySpec {
        val propertyType = when (style) {
            SpaceStyle.Property -> MutableFeatureFlagSpec::class
            SpaceStyle.Delegate -> MutableFeatureFlagValue::class
        }
            .asClassName()
            .parameterizedBy(TypeVariableName(flag.valueType))

        val propertySpec = PropertySpec.builder(flag.name, propertyType, KModifier.PUBLIC)
            .mutable(false)

        if (flag.deprecated) {
            val deprecatedAnnotationSpec =  AnnotationSpec.builder(Deprecated::class)
                .addMember("%S", "This feature flag is deprecated.")
                .build()
            propertySpec.addAnnotation(deprecatedAnnotationSpec)
        }

        val kDoc = flag.kDoc ?: flag.description
        if (kDoc != null) {
            propertySpec.addKdoc(kDoc)
        }

        when (style) {
            SpaceStyle.Property ->
                propertySpec.initializer(
                    """
                    MutableFeatureFlagSpec(
                        id = %S,
                        defaultValue = %L,
                        valueType = %L::class,
                        description = %S,
                    )
                """.trimIndent(),
                    flag.id, flag.defaultValue, flag.valueType, flag.description,
                )

            SpaceStyle.Delegate ->
                propertySpec.delegate(
                    """
                mutable(
                    id = %S,
                    defaultValue = %L,
                    valueType = %L::class,
                    description = %S,
                )
            """.trimIndent(),
                    flag.id, flag.defaultValue, flag.valueType, flag.description,
                )
        }

        return propertySpec.build()
    }

    private fun flowPropertySpec(flag: FlowFeatureFlagModel, style: SpaceStyle): PropertySpec {
        val propertyType = when (style) {
            SpaceStyle.Property -> FlowFeatureFlagSpec::class
            SpaceStyle.Delegate -> FlowFeatureFlagValue::class
        }
            .asClassName()
            .parameterizedBy(TypeVariableName(flag.valueType))

        val propertySpec = PropertySpec.builder(flag.name, propertyType, KModifier.PUBLIC)
            .mutable(false)

        if (flag.deprecated) {
            val deprecatedAnnotationSpec =  AnnotationSpec.builder(Deprecated::class)
                .addMember("%S", "This feature flag is deprecated.")
                .build()
            propertySpec.addAnnotation(deprecatedAnnotationSpec)
        }

        val kDoc = flag.kDoc ?: flag.description
        if (kDoc != null) {
            propertySpec.addKdoc(kDoc)
        }

        val flagStartedStrategy = MemberName(
            SharingStarted.Companion::class.asClassName(),
            when (flag.startedStrategy) {
                FlowSharingStarted.Lazily -> "Lazily"
                FlowSharingStarted.Eagerly -> "Eagerly"
            },
        )

        when (style) {
            SpaceStyle.Property ->
                propertySpec.initializer(
                    """
                    FlowFeatureFlagSpec(
                        id = %S,
                        defaultValue = %L,
                        valueType = %L::class,
                        description = %S,
                        startedStrategy = %M,
                    )
                """.trimIndent(),
                    flag.id, flag.defaultValue, flag.valueType, flag.description, flagStartedStrategy,
                )

            SpaceStyle.Delegate ->
                propertySpec.delegate(
                    """
                flow(
                    id = %S,
                    defaultValue = %L,
                    valueType = %L::class,
                    description = %S,
                    startedStrategy = %M,
                )
            """.trimIndent(),
                    flag.id, flag.defaultValue, flag.valueType, flag.description, flagStartedStrategy,
                )
        }

        return propertySpec.build()
    }
}
