package io.github.pavelannin.vexillum.plugin.generator

import io.github.pavelannin.vexillum.plugin.dsl.VexillumSpaceDsl
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.work.InputChanges
import java.io.File

open class CodeGeneratorTask : DefaultTask() {
    @get:Input
    lateinit var packageClass: String
    @get:Input
    lateinit var spaces: List<VexillumSpaceDsl>
    @get:OutputDirectory
    lateinit var outputDir: File

    @TaskAction
    fun execute(inputChanges: InputChanges) {
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw GradleException("Vexillum: Not found output dir")
        }

        if (spaces.any { space -> space.isDelegateStyle }) {
            val fileSpec = CodeGenerator.genSingletonFileSpec(packageClass)
            fileSpec.writeTo(outputDir)
        }

        for (space in spaces) {
            val fileSpec = CodeGenerator.genSpaceFileSpec(space, packageClass)
            fileSpec.writeTo(outputDir)
        }
    }
}
