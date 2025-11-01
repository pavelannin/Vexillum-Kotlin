plugins {
    alias(deps.plugins.kotlin.multiplatform)
    alias(deps.plugins.mokkery)
    id("io.github.pavelannin.multiplatform")
    id("io.github.pavelannin.publish")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(deps.kotlin.coroutines.core)
        }
        commonTest.dependencies {
            implementation(deps.kotlin.test)
            implementation(deps.kotlin.coroutines.test)
            implementation(deps.test.turbine)
        }
    }
}

publish {
    artifactId = "vexillum"
    version = property("publish.core.version").toString()
}
