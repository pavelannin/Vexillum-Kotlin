package io.github.pavelannin.vexillum.plugin

import io.github.pavelannin.vexillum.plugin.dsl.SpaceDsl
import java.io.File
import java.io.Serializable

open class Extensions : Serializable {
    var packageClass: String? = null

    internal val spacesDSL = mutableListOf<SpaceDsl>()
    internal val filesYAML = mutableListOf<File>()

    fun space(name: String, init: SpaceDsl.() -> Unit) {
        val space = SpaceDsl(name).apply(init)
        spacesDSL += space
    }

    fun fromYamlFile(file: File) {
        filesYAML += file
    }
}
