package io.github.pavelannin.vexillum.plugin

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import io.github.pavelannin.vexillum.plugin.generator.CodeGeneratorTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

class VexillumPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val vexillumExt = project.extensions.create("vexillum", VexillumExtensions::class.java)
        val appExt = project.extensions.findByType(BaseAppModuleExtension::class.java)
        val libraryExt = project.extensions.findByType(LibraryExtension::class.java)

        project.dependencies.extensions.add("vexillum", Dependencies(project))

        val outputDir = project.layout.buildDirectory.dir("generated/source/vexillum")
        val generateVexillumTask = project.tasks.register("vexillumGenerateFiles", CodeGeneratorTask::class.java) { task ->
            val androidNamespace = appExt?.namespace ?: libraryExt?.namespace
            task.packageClass = checkNotNull(vexillumExt.packageClass ?: androidNamespace) {
                "The required 'packageClass' parameter is null."
            }
            task.spaces = vexillumExt.spaces
            task.outputDir = outputDir.get().asFile
        }

        project.afterEvaluate {
            val kmpExt = project.kotlinExtension as? KotlinMultiplatformExtension
            if (kmpExt != null) {
                kmpExt.sourceSets
                    .filter { set -> set.name.endsWith("Main") }
                    .forEach { set -> set.kotlin.srcDir(outputDir) }

                project.tasks.named("compileKotlin")
                    .configure { task -> task.dependsOn(generateVexillumTask) }
                return@afterEvaluate
            }

            if (appExt != null || libraryExt != null) {
                val ext = checkNotNull(appExt ?: libraryExt)
                ext.sourceSets.forEach { set -> set.kotlin.srcDir(outputDir) }

                project.tasks.named("preBuild")
                    .configure { task -> task.dependsOn(generateVexillumTask) }
                return@afterEvaluate
            }

            val javaExt = project.extensions.findByType(JavaPluginExtension::class.java)
            if (javaExt != null) {
                javaExt.sourceSets.forEach { set -> set.java.srcDir(outputDir) }

                project.tasks.named("compileJava")
                    .configure { task -> task.dependsOn(generateVexillumTask) }
            }
        }
    }
}
