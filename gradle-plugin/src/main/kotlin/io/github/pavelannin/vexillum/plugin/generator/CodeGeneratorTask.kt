package io.github.pavelannin.vexillum.plugin.generator

import com.charleskorn.kaml.PolymorphismStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import io.github.pavelannin.vexillum.plugin.dsl.FeatureFlagDsl
import io.github.pavelannin.vexillum.plugin.dsl.FlowFeatureFlagDsl
import io.github.pavelannin.vexillum.plugin.dsl.ImmutableFeatureFlagDsl
import io.github.pavelannin.vexillum.plugin.dsl.MutableFeatureFlagDsl
import io.github.pavelannin.vexillum.plugin.dsl.SpaceDsl
import io.github.pavelannin.vexillum.plugin.model.FeatureFlagModel
import io.github.pavelannin.vexillum.plugin.model.FlowFeatureFlagModel
import io.github.pavelannin.vexillum.plugin.model.ImmutableFeatureFlagModel
import io.github.pavelannin.vexillum.plugin.model.MutableFeatureFlagModel
import io.github.pavelannin.vexillum.plugin.model.SpaceModel
import io.github.pavelannin.vexillum.plugin.model.SpaceStyle
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.InputChanges
import java.io.File

open class CodeGeneratorTask : DefaultTask() {
    @get:Input lateinit var packageClass: String
    @get:Input lateinit var spacesDSL: List<SpaceDsl>
    @get:Input lateinit var filesYAML: List<File>
    @get:OutputDirectory lateinit var outputDir: File

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw GradleException("Vexillum: Not found output dir")
        }

        val spaces = parseYaml(filesYAML) + convertDsl(spacesDSL)

        // Unique name
        spaces.fold(emptySet<String>()) { acc, space ->
            val name = space.name
            check(!acc.contains(name)) { "The space named '${name}' is already there" }
            acc + name
        }
        // Unique id
        spaces.flatMap { space -> space.flags }
            .fold(emptySet<String>()) { acc, flag ->
                val id = flag.id
                check(!acc.contains(id)) {
                    "The feature flag id '${id}' is already there"
                }
                acc + id
            }


        if (spaces.any { space -> space.style == SpaceStyle.Delegate }) {
            val fileSpec = CodeGenerator.genSingletonFileSpec(packageClass)
            fileSpec.writeTo(outputDir)
        }

        for (space in spaces) {
            val fileSpec = CodeGenerator.genSpaceFileSpec(space, packageClass)
            fileSpec.writeTo(outputDir)
        }
    }
}

private fun parseYaml(files: List<File>): List<SpaceModel> {
    val yamlConfig = Yaml.default.configuration.copy(
        polymorphismStyle = PolymorphismStyle.Property,
    )
    val yaml = Yaml(configuration = yamlConfig)

    return files.map { file -> yaml.decodeFromStream<SpaceModel>(file.inputStream()) }
}

private fun convertDsl(spaces: List<SpaceDsl>): List<SpaceModel> {
    fun <T> T?.check(propertyName: String, featureName: String): T {
        return checkNotNull(this) {
            "The required '$propertyName' parameter for the '$featureName' feature flag is not specified."
        }
    }

    fun convent(dsl: SpaceDsl, transform: (FeatureFlagDsl) -> FeatureFlagModel): SpaceModel {
        return SpaceModel(
            name = dsl.name,
            flags = dsl.featureFlags.map(transform),
            kDoc = dsl.kDoc,
            deprecated = dsl.deprecated,
            style = dsl.style,
        )
    }
    fun convent(dsl: FeatureFlagDsl): FeatureFlagModel {
        return when (dsl) {
            is ImmutableFeatureFlagDsl ->
                ImmutableFeatureFlagModel(
                    name = dsl.name,
                    id = dsl.id.check("id", dsl.name),
                    valueType = dsl.valueType.check("valueType", dsl.name),
                    description = dsl.description,
                    kDoc = dsl.kDoc,
                    deprecated = dsl.deprecated,
                    value = dsl.value.check("value", dsl.name),
                )

            is MutableFeatureFlagDsl ->
                MutableFeatureFlagModel(
                    name = dsl.name,
                    id = dsl.id.check("id", dsl.name),
                    valueType = dsl.valueType.check("valueType", dsl.name),
                    description = dsl.description,
                    kDoc = dsl.kDoc,
                    deprecated = dsl.deprecated,
                    defaultValue = dsl.defaultValue.check("defaultValue", dsl.name),
                )

            is FlowFeatureFlagDsl ->
                FlowFeatureFlagModel(
                    name = dsl.name,
                    id = dsl.id.check("id", dsl.name),
                    valueType = dsl.valueType.check("valueType", dsl.name),
                    description = dsl.description,
                    kDoc = dsl.kDoc,
                    deprecated = dsl.deprecated,
                    defaultValue = dsl.defaultValue.check("defaultValue", dsl.name),
                    startedStrategy = dsl.startedStrategy,
                )
        }
    }

    return spaces.map { dsl -> convent(dsl, ::convent) }
}
