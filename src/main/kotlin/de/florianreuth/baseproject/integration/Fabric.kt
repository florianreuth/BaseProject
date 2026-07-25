/*
 * This file is part of BaseProject - https://github.com/florianreuth/BaseProject
 * Copyright (C) 2024-2026 Florian Reuth <git@florianreuth.de> and contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.florianreuth.baseproject.integration

import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.kotlin.dsl.*
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

val Project.fabricApiVersion: String
    get() = property("fabric_api_version") as String

/**
 * Sets up Fabric development environment.
 * Requires the following properties:
 * - `fabric_loader_version`: The version of Fabric Loader to use
 * - `minecraft_version`: The version of Minecraft to target
 *
 * Optional properties:
 * - `fabric_kotlin_version`: The version of Fabric Kotlin to use
 * - `fabric_api_version`: The version of Fabric API to use
 * - `supported_minecraft_versions`: A comma-separated list of supported Minecraft versions
 */
fun Project.setupFabric() {
    plugins.apply("net.fabricmc.fabric-loom")

    dependencies {
        "implementation"("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        dependencies {
            "implementation"("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
        }
    }

    val accessWidenerFile = file("src/main/resources/${project.name.lowercase()}.accesswidener")
    if (accessWidenerFile.exists()) {
        extensions.getByType(LoomGradleExtensionAPI::class.java).apply {
            accessWidenerPath.set(accessWidenerFile)
        }
    }
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.parchmentmc.org/")
    }
    dependencies {
        "minecraft"("com.mojang:minecraft:${property("minecraft_version")}")
    }
    tasks.named<ProcessResources>("processResources").configure {
        val projectName = project.name
        val projectVersion = project.version
        val projectDescription = project.description
        val mcVersion = if (!project.hasProperty("supported_minecraft_versions")) {
            project.property("minecraft_version") as String
        } else {
            val supportedVersions = project.property("supported_minecraft_versions") as String
            supportedVersions.ifEmpty {
                project.property("minecraft_version") as String
            }
        }
        val latestCommitHash = latestCommitHash()
        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to projectVersion,
                    "implVersion" to "git-${projectName}-${projectVersion}:${latestCommitHash}",
                    "description" to projectDescription,
                    "mcVersion" to mcVersion,
                    "commitHash" to latestCommitHash,
                    "shortCommitHash" to latestCommitHash.take(7)
                )
            )
        }
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        extensions.getByType(KotlinJvmExtension::class.java).apply {
            compilerOptions.freeCompilerArgs.add("-Xjsr305=ignore")
        }
    }

    excludeRunFolder()
}

/**
 * Configures the project to use Jar-in-Jar (JiJ) dependencies.
 */
fun Project.configureJarInJar(): Configuration {
    return configurations.maybeCreate("jarInJar").also {
        configurations.getByName("implementation").extendsFrom(it)
        configurations.getByName("include").extendsFrom(it)
        configurations.getByName("api").extendsFrom(it)
    }
}

/**
 * Includes all transitive dependencies of the Jar-in-Jar configuration in the compiled JAR.
 */
fun Project.includeTransitiveJijDependencies() {
    val jijConfig = configurations.findByName("jarInJar") ?: return

    fun configure(targetName: String) {
        configurations.findByName(targetName)?.defaultDependencies {
            jijConfig.incoming.resolutionResult.allComponents.mapNotNull { it.id as? ModuleComponentIdentifier }
                .forEach { id ->
                    val notation = "${id.group}:${id.module}:${id.version}"
                    add(dependencies.create(notation) {
                        isTransitive = false
                    })
                }
        }
    }

    configure("api")
    configure("implementation")
    configure("include")
}
