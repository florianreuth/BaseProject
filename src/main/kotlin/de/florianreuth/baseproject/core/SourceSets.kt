/*
 * This file is part of BaseProject - https://github.com/florianreuth/BaseProject
 * Copyright (C) 2024-2026 Florian Reuth <git@florianreuth.de>
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

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.the

/**
 * Creates a bidirectional source set where both the new source set and the main source set depend on each other.
 */
fun Project.configureLinkedSourceSet(name: String) {
    val sourceSets = the<SourceSetContainer>()

    val main = sourceSets.getByName("main")
    val newSet = sourceSets.create(name) {
        compileClasspath += main.output + main.compileClasspath
        runtimeClasspath += compileClasspath

        main.runtimeClasspath += output + runtimeClasspath
    }

    tasks.named("jar", Jar::class.java).configure {
        from(newSet.output)
    }
}

/**
 * Creates a new source set and adds it to the project.
 */
fun Project.configureSourceSet(name: String) {
    val sourceSets = the<SourceSetContainer>()

    val main = sourceSets.getByName("main")
    val sourceSet = sourceSets.create(name)

    sourceSet.compileClasspath += main.output + main.compileClasspath
    sourceSet.runtimeClasspath += sourceSet.compileClasspath
}
