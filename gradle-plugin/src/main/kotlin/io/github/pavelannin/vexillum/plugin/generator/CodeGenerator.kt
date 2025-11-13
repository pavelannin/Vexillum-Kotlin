package io.github.pavelannin.vexillum.plugin.generator

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
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
import io.github.pavelannin.vexillum.plugin.dsl.VexillumFeatureFlagDsl
import io.github.pavelannin.vexillum.plugin.dsl.VexillumSpaceDsl
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

    fun genSpaceFileSpec(space: VexillumSpaceDsl, packageClass: String): FileSpec {
        val fileName = space.name.capitalized()

        val spaceTypeSpec = TypeSpec.objectBuilder(fileName)
            .addModifiers(KModifier.PUBLIC)

        if (space.isDeprecated) {
            val deprecatedAnnotationSpec =  AnnotationSpec.builder(Deprecated::class)
                .addMember("%S", "This space is deprecated.")
                .build()
            spaceTypeSpec.addAnnotation(deprecatedAnnotationSpec)
        }

        val kDoc = space.kDoc
        if (kDoc != null) {
            spaceTypeSpec.addKdoc(kDoc)
        }

        if (space.isDelegateStyle) {
            spaceTypeSpec.addSuperinterface(VexillumSpace::class, CodeBlock.of("VexillumSpace($SINGLETON_NAME)"))
        }

        for (flag in space.featureFlags) {
            val property = when (flag) {
                is VexillumFeatureFlagDsl.Immutable -> immutablePropertySpec(flag, space.isDelegateStyle)
                is VexillumFeatureFlagDsl.Mutable -> mutablePropertySpec(flag, space.isDelegateStyle)
                is VexillumFeatureFlagDsl.Flow -> flowPropertySpec(flag, space.isDelegateStyle)
            }
            spaceTypeSpec.addProperty(property)
        }

        return FileSpec.builder(packageClass, fileName)
            .addType(spaceTypeSpec.build())
            .build()
    }

    private fun immutablePropertySpec(
        flag: VexillumFeatureFlagDsl.Immutable,
        delegateStyle: Boolean,
    ): PropertySpec {
        val flagId = checkNotNull(flag.id) {
            "The required 'id' parameter for the '${flag.name}' feature flag is not specified."
        }
        val flagValue = checkNotNull(flag.value) {
            "The required 'value' parameter for the '${flag.value}' feature flag is not specified."
        }
        val flagTypeValue = checkNotNull(flag.valueType) {
            "The required 'valueType' parameter for the '${flag.name}' feature flag is not specified."
        }

        val propertyType = (if (delegateStyle) ImmutableFeatureFlagValue::class else ImmutableFeatureFlagSpec::class)
            .asClassName()
            .parameterizedBy(TypeVariableName(flagTypeValue))

        val propertySpec = PropertySpec.builder(flag.name, propertyType, KModifier.PUBLIC)
            .mutable(false)

        if (flag.isDeprecated) {
            val deprecatedAnnotationSpec =  AnnotationSpec.builder(Deprecated::class)
                .addMember("%S", "This feature flag is deprecated.")
                .build()
            propertySpec.addAnnotation(deprecatedAnnotationSpec)
        }

        val kDoc = flag.kDoc ?: flag.description
        if (kDoc != null) {
            propertySpec.addKdoc(kDoc)
        }


        if (delegateStyle) {
            propertySpec.delegate(
                """
                immutable(
                    id = %S,
                    value = %L,
                    valueType = %L::class,
                    description = %S,
                )
            """.trimIndent(),
                flagId, flagValue,flagTypeValue, flag.description,
            )
        } else {
            propertySpec.initializer(
                """
                    ImmutableFeatureFlagSpec(
                        id = %S,
                        value = %L,
                        valueType = %L::class,
                        description = %S,
                    )
                """.trimIndent(),
                flagId, flagValue,flagTypeValue, flag.description,
            )
        }
        return propertySpec.build()
    }

    private fun mutablePropertySpec(
        flag: VexillumFeatureFlagDsl.Mutable,
        delegateStyle: Boolean,
    ): PropertySpec {
        val flagId = checkNotNull(flag.id) {
            "The required 'id' parameter for the '${flag.name}' feature flag is not specified."
        }
        val flagValue = checkNotNull(flag.defaultValue) {
            "The required 'defaultValue' parameter for the '${flag.defaultValue}' feature flag is not specified."
        }
        val flagTypeValue = checkNotNull(flag.valueType) {
            "The required 'valueType' parameter for the '${flag.name}' feature flag is not specified."
        }

        val propertyType = (if (delegateStyle) MutableFeatureFlagValue::class else MutableFeatureFlagSpec::class)
            .asClassName()
            .parameterizedBy(TypeVariableName(flagTypeValue))

        val propertySpec = PropertySpec.builder(flag.name, propertyType, KModifier.PUBLIC)
            .mutable(false)

        if (flag.isDeprecated) {
            val deprecatedAnnotationSpec =  AnnotationSpec.builder(Deprecated::class)
                .addMember("%S", "This feature flag is deprecated.")
                .build()
            propertySpec.addAnnotation(deprecatedAnnotationSpec)
        }

        val kDoc = flag.kDoc ?: flag.description
        if (kDoc != null) {
            propertySpec.addKdoc(kDoc)
        }


        if (delegateStyle) {
            propertySpec.delegate(
                """
                mutable(
                    id = %S,
                    defaultValue = %L,
                    valueType = %L::class,
                    description = %S,
                )
            """.trimIndent(),
                flagId, flagValue,flagTypeValue, flag.description,
            )
        } else {
            propertySpec.initializer(
                """
                    MutableFeatureFlagSpec(
                        id = %S,
                        defaultValue = %L,
                        valueType = %L::class,
                        description = %S,
                    )
                """.trimIndent(),
                flagId, flagValue,flagTypeValue, flag.description,
            )
        }
        return propertySpec.build()
    }

    private fun flowPropertySpec(
        flag: VexillumFeatureFlagDsl.Flow,
        delegateStyle: Boolean,
    ): PropertySpec {
        val flagId = checkNotNull(flag.id) {
            "The required 'id' parameter for the '${flag.name}' feature flag is not specified."
        }
        val flagValue = checkNotNull(flag.defaultValue) {
            "The required 'defaultValue' parameter for the '${flag.defaultValue}' feature flag is not specified."
        }
        val flagTypeValue = checkNotNull(flag.valueType) {
            "The required 'valueType' parameter for the '${flag.name}' feature flag is not specified."
        }

        val propertyType = (if (delegateStyle) FlowFeatureFlagValue::class else FlowFeatureFlagSpec::class)
            .asClassName()
            .parameterizedBy(TypeVariableName(flagTypeValue))

        val propertySpec = PropertySpec.builder(flag.name, propertyType, KModifier.PUBLIC)
            .mutable(false)

        if (flag.isDeprecated) {
            val deprecatedAnnotationSpec =  AnnotationSpec.builder(Deprecated::class)
                .addMember("%S", "This feature flag is deprecated.")
                .build()
            propertySpec.addAnnotation(deprecatedAnnotationSpec)
        }

        val kDoc = flag.kDoc ?: flag.description
        if (kDoc != null) {
            propertySpec.addKdoc(kDoc)
        }

        val flagStartedStrategy = when (flag.startedStrategy) {
            VexillumFeatureFlagDsl.Flow.SharingStarted.Lazily -> SharingStarted.Lazily
            VexillumFeatureFlagDsl.Flow.SharingStarted.Eagerly -> SharingStarted.Eagerly
        }
        if (delegateStyle) {
            propertySpec.delegate(
                """
                flow(
                    id = %S,
                    defaultValue = %L,
                    valueType = %L::class,
                    description = %S,
                    startedStrategy = %L,
                )
            """.trimIndent(),
                flagId, flagValue,flagTypeValue, flag.description, flagStartedStrategy,
            )
        } else {
            propertySpec.initializer(
                """
                    FlowFeatureFlagSpec(
                        id = %S,
                        defaultValue = %L,
                        valueType = %L::class,
                        description = %S,
                        startedStrategy = %L,
                    )
                """.trimIndent(),
                flagId, flagValue,flagTypeValue, flag.description, flagStartedStrategy,
            )
        }
        return propertySpec.build()
    }
}
