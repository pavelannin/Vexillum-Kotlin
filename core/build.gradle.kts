plugins {
    alias(deps.plugins.kotlin.multiplatform)
    id("io.github.pavelannin.multiplatform")
    id("io.github.pavelannin.publish")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(deps.kotlin.coroutines.core)
        }
    }
}

publish {
    artifactId = "vexillum"
    version = property("publish.core.version").toString()
}
