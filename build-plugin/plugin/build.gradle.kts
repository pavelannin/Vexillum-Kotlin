import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

gradlePlugin {
    plugins {
        register("publish") {
            id = "io.github.pavelannin.publish"
            implementationClass = "io.github.pavelannin.publish.PublishPlugin"
        }
        register("multiplatform") {
            id = "io.github.pavelannin.multiplatform"
            implementationClass = "io.github.pavelannin.multiplatform.MultiplatformPlugin"
        }
    }
}

dependencies {
    implementation(deps.plugin.kotlin.multiplatform)
    implementation(deps.plugin.maven.publish)
}
