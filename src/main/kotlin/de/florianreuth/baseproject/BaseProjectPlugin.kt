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

import de.florianreuth.baseproject.core.*
import de.florianreuth.baseproject.publishing.*
import org.gradle.api.Plugin
import org.gradle.api.Project

open class BaseProjectPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.logger.lifecycle("BaseProject: " + javaClass.getPackage().implementationVersion)
    }

}

/**
 * Sets up the project with a common configuration.
 *
 * Required project properties:
 * - `project_group`: The group ID of the project
 * - `project_version`: The version of the project
 * - `project_description`: A short description of the project
 * - `project_name`: The archive base name for outputs
 * - `jvm_version`: The Java version to compile against
 */
fun Project.setupProject() {
    configureProjectMetadata()
    configureDefaultRepositories()
    configureJvmToolchain()
    configureUtf8Encoding()
    hideBuildWarnings()
    suffixLicenseFile()
}

/**
 * Sets up publishing for the project.
 *
 * Requires the following project properties:
 * - `github_account`: The GitHub account id.
 * - `github_repository`: The GitHub repository name.
 * - `publish_owner_name`: The publication owner name.
 * - `publish_owner_mail`: The publication owner email.
 */
fun Project.setupPublishing() {
    configureGitHubPublishing()
    configureReposiliteRepository()
    configureSonatypeRepository()
}

/**
 * Sets up publishing for the project using ViaVersion.
 *
 * Requires the following project properties:
 * - `publish_owner_name`: The publication owner name.
 * - `publish_owner_mail`: The publication owner email.
 */
fun Project.setupViaPublishing() {
    configureViaVersionRepository()

    setProperty("github_account", "ViaVersion")
    setProperty("publish_license", "GPL-3.0")
    configureGitHubPublishing()
}
