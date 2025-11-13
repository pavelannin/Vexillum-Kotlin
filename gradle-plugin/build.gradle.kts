import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(deps.plugins.kotlin.jvm)
    alias (deps.plugins.gradle.publish)
    `java-gradle-plugin`
    id("io.github.pavelannin.publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

gradlePlugin {
    plugins {
        create("VexillumPlugin") {
            id = "io.github.pavelannin.vexillum"
            displayName = "Vexillum Plugin"
            implementationClass = "io.github.pavelannin.vexillum.plugin.VexillumPlugin"
        }
    }
}

val buildConfigDir get() = project.layout.buildDirectory.dir("generated/buildconfig")

val buildConfig = tasks.register("buildConfig", GenerateBuildConfig::class.java) {
    generatedOutputDir.set(buildConfigDir)

    classFqName.set("io.github.pavelannin.vexillum.plugin.VexillumBuildConfig")

    fields.put("coreVersion", project.property("publish.core.version").toString())
    fields.put("persistentVersion", project.property("publish.persistent.version").toString())
}

tasks.named("compileKotlin", KotlinCompilationTask::class) {
    dependsOn(buildConfig)
}
sourceSets.main.configure {
    kotlin.srcDir(buildConfig.flatMap { it.generatedOutputDir })
}

dependencies {
    implementation(projects.core)
    implementation(deps.plugin.kotlin.multiplatform)
    implementation(deps.kotlin.poet)
    implementation(deps.androidx.gradle)
}

publish {
    artifactId = "vexillum-plugin"
    version = property("publish.plugin.version").toString()
}

open class GenerateBuildConfig : DefaultTask() {
    @get:Input
    val classFqName: Property<String> = project.objects.property()
    @get:Input
    val fields: MapProperty<String, Any> = project.objects.mapProperty()
    @get:OutputDirectory
    val generatedOutputDir: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun execute() {
        val dir = generatedOutputDir.get().asFile
        dir.deleteRecursively()
        dir.mkdirs()

        val fqName = classFqName.get()
        val parts = fqName.split(".")
        val className = parts.last()
        val file = dir.resolve("$className.kt")
        val content = buildString {
            if (parts.size > 1) {
                appendLine("package ${parts.dropLast(1).joinToString(".")}")
            }

            appendLine()
            appendLine("object $className {")
            for ((k, v) in fields.get().entries.sortedBy { it.key }) {
                appendLine("    const val $k = ${if (v is String) "\"$v\"" else v.toString()}")
            }
            appendLine("}")
        }
        file.writeText(content)
    }
}