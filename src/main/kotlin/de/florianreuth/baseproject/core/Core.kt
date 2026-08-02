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

package de.florianreuth.baseproject.core

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension

/**
 * Configures the default repositories used by the project.
 */
fun Project.configureDefaultRepositories() {
    repositories {
        mavenCentral()
        maven("https://maven.florianreuth.de/releases")
    }
}

/**
 * Configures the project metadata, such as group, version, and description.
 *
 * Requires the following properties:
 * - `project_group`: The group ID of the project
 * - `project_version`: The version of the project
 * - `project_description`: A short description of the project
 * - `project_name`: The archive base name for outputs
 */
fun Project.configureProjectMetadata() {
    group = property("project_group") as String
    version = property("project_version") as String
    description = property("project_description") as String

    if (this == rootProject) {
        val projectName = findProperty("project_name") as? String ?: return
        apply(plugin = "base")
        extensions.getByType(BasePluginExtension::class.java).apply {
            archivesName.set(projectName)
        }
    }
}

/**
 * Configures the Java (and if applicable, Kotlin) toolchain for the project.
 * Requires the `jvm_version` project property to be set.
 */
fun Project.configureJvmToolchain() {
    val version = (project.property("jvm_version") as String).toInt()

    apply(plugin = "java-library")
    extensions.getByType(JavaPluginExtension::class.java).apply {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(version))
        }
        sourceCompatibility = JavaVersion.toVersion(version)
        targetCompatibility = JavaVersion.toVersion(version)
    }

    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        extensions.getByType(KotlinJvmExtension::class.java).apply {
            jvmToolchain(version)
        }
    }
}

/**
 * Configures the project to use UTF-8 encoding for all Java source compilation tasks.
 */
fun Project.configureUtf8Encoding() {
    tasks.withType(JavaCompile::class.java).configureEach {
        options.encoding = "UTF-8"
    }
    tasks.withType(Javadoc::class.java).configureEach {
        options.encoding = "UTF-8"
    }
}

/**
 * Suffixes the LICENSE file in the JAR output with the project name to avoid conflicts when multiple projects are combined.
 */
fun Project.suffixLicenseFile() {
    tasks.named("jar", Jar::class.java).configure {
        val projectName = project.name

        from("LICENSE") {
            rename { "LICENSE_$projectName" }
        }
    }
}
