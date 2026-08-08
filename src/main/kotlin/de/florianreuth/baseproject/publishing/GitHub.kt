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

package de.florianreuth.baseproject.publishing

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.extraProperties

/**
 * Configures publishing from a GitHub repository.
 * Requires the following properties:
 * - `github_account`: The GitHub account name.
 * - `github_repository`: The GitHub repository name.
 */
fun Project.configureGitHubPublishing() {
    val account = property("github_account") as String
    val repository = property("github_repository") as String

    extraProperties.set("publish_distribution", "github.com/$account/$repository")
    extraProperties.set("publish_license_url", "https://github.com/$account/$repository/blob/main/LICENSE")
    configurePublishing()
}
