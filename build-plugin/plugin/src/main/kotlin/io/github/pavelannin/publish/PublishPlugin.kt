package io.github.pavelannin.publish

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishBasePlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

class PublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val ext = target.extensions.create<PublishExtension>("publish")

        if (!target.plugins.hasPlugin("com.vanniktech.maven.publish")) {
            target.plugins.apply("com.vanniktech.maven.publish")
        }

        target.afterEvaluate {
            plugins.withType<MavenPublishBasePlugin> {
                extensions.configure<MavenPublishBaseExtension> {
                    publishToMavenCentral()
                    signAllPublications()
                    coordinates("io.github.pavelannin", ext.artifactId, ext.version)
                    pom {
                        name.set(ext.artifactId)
                        description.set("Vexillum is a multiplatform Kotlin framework that provides toggling features.")
                        url.set("https://github.com/pavelannin/Vexillum-Kotlin")
                        licenses {
                            license {
                                name.set("Apache-2.0")
                                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        developers {
                            developer {
                                name.set("Pavel Annin")
                                email.set("pavelannin.dev@gmail.com")
                            }
                        }
                        scm {
                            connection.set("scm:git:github.com/pavelannin/Vexillum-Kotlin.git")
                            developerConnection.set("scm:git:ssh://github.com/pavelannin/Vexillum-Kotlin.git")
                            url.set("https://github.com/pavelannin/Vexillum-Kotlin/tree/main")
                        }
                    }
                }
            }
        }
    }
}
