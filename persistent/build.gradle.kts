plugins {
    alias(deps.plugins.kotlin.multiplatform)
    alias(deps.plugins.kotlin.serialization)
    alias(deps.plugins.android.library)
    alias(deps.plugins.mokkery)
    id("io.github.pavelannin.multiplatform")
    id("io.github.pavelannin.publish")
//    id("io.github.pavelannin.vexillum")
}

kotlin {
    androidTarget()

    sourceSets {
        commonMain.dependencies {
            api(projects.core)
            api(deps.kotlin.serialization.json)
            api(deps.androidx.datastore)
            api(deps.androidx.datastore.preferences)
        }
        commonTest.dependencies {
            implementation(deps.kotlin.test)
            implementation(deps.kotlin.coroutines.test)
            implementation(deps.test.turbine)
        }
    }
}

android {
    namespace = "io.github.pavelannin.vexillum.persistent"
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
    artifactId = "vexillum-persistent"
    version = property("publish.persistent.version").toString()
}
