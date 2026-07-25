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

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.dependencies

/**
 * Configures the JUnit platform for tests.
 */
fun Project.configureTest() {
    tasks.withType(Test::class.java).configureEach {
        useJUnitPlatform()
        testLogging {
            events(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
            exceptionFormat = TestExceptionFormat.FULL
        }

        val runDir = layout.projectDirectory.dir("run").asFile
        if (runDir.isDirectory) {
            workingDir = runDir
        }
    }
    pluginManager.withPlugin("net.fabricmc.fabric-loom") {
        dependencies {
            "testImplementation"("net.fabricmc:fabric-loader-junit:${property("fabric_loader_version")}")
        }
    }
}
