plugins {
    alias(deps.plugins.kotlin.multiplatform)
    alias(deps.plugins.android.library)
    alias(deps.plugins.mokkery)
    id("io.github.pavelannin.multiplatform")
    id("io.github.pavelannin.publish")
}

kotlin {
    androidTarget()

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

android {
    namespace = "io.github.pavelannin.vexillum"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

publish {
    artifactId = "vexillum"
    version = property("publish.core.version").toString()
}
