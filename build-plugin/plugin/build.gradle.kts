plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
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
