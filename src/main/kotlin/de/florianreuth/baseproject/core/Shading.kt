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
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.jvm.tasks.Jar

/**
 * Configures a custom `shadedDependencies` configuration used to embed shaded dependencies in the JAR and also include
 * them as normal dependencies.
 */
fun Project.configureShadedDependencies(): Configuration {
    val configuration = createEmbeddingConfiguration("shadedDependencies")
    configurations.findByName("implementation")?.extendsFrom(configuration)
    return configuration
}

/**
 * Configures a custom `embeddedDependencies` configuration used to embed dependencies in the JAR.
 */
fun Project.configureEmbeddedDependencies(): Configuration = createEmbeddingConfiguration("embeddedDependencies")

private fun Project.createEmbeddingConfiguration(name: String): Configuration {
    val configuration = configurations.create(name).apply {
        isCanBeResolved = true
        isCanBeConsumed = true
    }
    tasks.named("jar", Jar::class.java).configure {
        dependsOn(configuration)
        from({ configuration.map { zipTree(it) } }) {
            exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    return configuration
}
