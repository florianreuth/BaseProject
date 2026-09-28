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

package de.florianreuth.baseproject/*
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

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.the

/**
 * Creates a source set that sees the main source set and is visible to it; its output is added to the jar.
 *
 * @param name the name of the new source set
 */
fun Project.configureLinkedSourceSet(name: String) {
    val sourceSets = the<SourceSetContainer>()

    val main = sourceSets.getByName("main")
    val newSet = sourceSets.create(name) {
        compileClasspath += main.output + main.compileClasspath
        runtimeClasspath += compileClasspath

        main.runtimeClasspath += output + runtimeClasspath
    }

    tasks.named<Jar>("jar") {
        from(newSet.output)
    }
}

/**
 * Creates a source set that sees the main source set; its output is not added to the jar.
 *
 * @param name the name of the new source set
 */
fun Project.configureSourceSet(name: String) {
    val sourceSets = the<SourceSetContainer>()

    val main = sourceSets.getByName("main")
    val sourceSet = sourceSets.create(name)

    sourceSet.compileClasspath += main.output + main.compileClasspath
    sourceSet.runtimeClasspath += sourceSet.compileClasspath
}
