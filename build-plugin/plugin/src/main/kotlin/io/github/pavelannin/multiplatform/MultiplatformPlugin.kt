package io.github.pavelannin.multiplatform

import io.github.pavelannin.publish.PublishExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

class MultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target.multiplatformExtension) {
            explicitApi()
            jvmToolchain(8)

            jvm()

            iosArm64()
            iosX64()
            iosSimulatorArm64()

            sourceSets.all {
                languageSettings.optIn("kotlin.contracts.ExperimentalContracts")
            }
        }
        target.tasks.withType<Jar> {
            from(target.rootDir.resolve("LICENSE")) { into("META-INF") }
        }
    }
}

internal val Project.multiplatformExtension: KotlinMultiplatformExtension
    get() = kotlinExtension as KotlinMultiplatformExtension

