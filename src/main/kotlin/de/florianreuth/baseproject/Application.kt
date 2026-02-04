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

package de.florianreuth.baseproject

import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.attributes

/**
 * Configures the application JAR to specify the main class in the manifest.
 * Also excludes the `run/` folder from IntelliJ's project model.
 *
 * @param mainClass the fully qualified name of the main class to use in the JAR manifest.
 * Defaults to the `application_main` project property if not specified.
 */
fun Project.configureApplication(mainClass: String = project.property("application_main") as String) {
    tasks.named("jar", Jar::class.java).configure {
        manifest {
            attributes("Main-Class" to mainClass)
        }
    }

    excludeRunFolder()
}
