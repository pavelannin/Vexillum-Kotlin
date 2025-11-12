plugins {
    alias(deps.plugins.kotlin.multiplatform) apply false
    alias(deps.plugins.kotlin.jvm) apply false
    alias(deps.plugins.kotlin.serialization) apply false
    alias(deps.plugins.android.library) apply false
    alias (deps.plugins.mokkery) apply false
    alias (deps.plugins.gradle.publish) apply false
}
