import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    signing
    `maven-publish`
    id("com.gradle.plugin-publish") version "2.0.0"
}

group = property("project_group") as String
version = property("project_version") as String
description = property("project_description") as String

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
    compileOnly("net.fabricmc:fabric-loom:1.10-SNAPSHOT")
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    jar {
        val projectName = project.name
        from("LICENSE") {
            rename { "LICENSE_${projectName}" }
        }

        val projectVersion = project.version
        manifest {
            attributes(mapOf("Implementation-Version" to projectVersion))
        }
    }

    publishPlugins {
        notCompatibleWithConfigurationCache("plugin-publish plugin is not compatible with the configuration cache yet.")
    }
}

gradlePlugin {
    website = "https://github.com/florianreuth/BaseProject"
    vcsUrl = "https://github.com/florianreuth/BaseProject.git"
    plugins {
        create("baseProjectPlugin") {
            id = "${project.group}.${project.name}"
            implementationClass = "de.florianreuth.baseproject.BaseProjectPlugin"
            displayName = "BaseProject Convention Plugin"
            description = project.description
            tags = listOf("fabric", "convention")
        }
    }
}

val reposiliteUsername = findProperty("reposiliteUsername") as String?
val reposilitePassword = findProperty("reposilitePassword") as String?
if (reposiliteUsername != null && reposilitePassword != null) {
    publishing {
        repositories {
            maven {
                name = "reposilite"
                url = uri(
                    "https://maven.florianreuth.de/" +
                        if (project.version.toString().contains("SNAPSHOT")) "snapshots" else "releases"
                )
                credentials {
                    username = reposiliteUsername
                    password = reposilitePassword
                }
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }
}

signing {
    isRequired = false
    sign(publishing.publications)
}
