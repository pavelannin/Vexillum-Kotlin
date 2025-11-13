package io.github.pavelannin.vexillum.plugin

import org.gradle.api.Project

class Dependencies(project: Project) {
    val core: String = "io.github.pavelannin:vexillum:${VexillumBuildConfig.coreVersion}"
    val persistent: String = "io.github.pavelannin:vexillum-persistent:${VexillumBuildConfig.persistentVersion}"
}
