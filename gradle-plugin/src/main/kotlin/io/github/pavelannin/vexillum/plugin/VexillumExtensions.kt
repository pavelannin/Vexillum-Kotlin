package io.github.pavelannin.vexillum.plugin

import io.github.pavelannin.vexillum.plugin.dsl.VexillumSpaceDsl
import java.io.File
import java.io.Serializable

open class VexillumExtensions : Serializable {
    var packageClass: String? = null

    internal val spaces = mutableListOf<VexillumSpaceDsl>()
    internal val files = mutableListOf<File>()

    fun space(name: String, init: VexillumSpaceDsl.() -> Unit) {
        val space = VexillumSpaceDsl(name).apply(init)
        spaces += space
    }

    fun fromFile(file: File) {
        files += file
    }
}
