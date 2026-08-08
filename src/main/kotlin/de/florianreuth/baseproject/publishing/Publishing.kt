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
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.plugins.signing.SigningExtension

/**
 * Configures Maven publishing and signing for the project.
 * Requires the following properties:
 * - `project_name`: The human-readable project name.
 * - `publish_distribution`: The distribution host (e.g. `github.com/owner/repo`).
 * - `publish_owner_id`: The developer id.
 * - `publish_owner_name`: The developer name.
 * - `publish_owner_mail`: The developer email.
 *
 * Optional properties:
 * - `publish_license`: The license name (defaults to `Apache-2.0`).
 * - `publish_license_url`: The license URL (defaults to the Apache 2.0 license URL).
 */
fun Project.configurePublishing() {
    val projectName = property("project_name") as String

    val distribution = property("publish_distribution") as String

    val ownerId = property("publish_owner_id") as String
    val ownerName = property("publish_owner_name") as String
    val ownerMail = property("publish_owner_mail") as String

    val licenseName = findProperty("publish_license") as String? ?: "Apache-2.0"
    val licenseUrl = findProperty("publish_license_url") as String? ?: "https://www.apache.org/licenses/LICENSE-2.0"

    generateJavadocJar()

    apply(plugin = "maven-publish")
    extensions.getByType(PublishingExtension::class.java).apply {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()

                from(components["java"])

                pom {
                    name.set(projectName)
                    description.set(project.description)
                    url.set("https://$distribution")
                    licenses {
                        license {
                            name.set(licenseName)
                            url.set(licenseUrl)
                        }
                    }
                    developers {
                        developer {
                            id.set(ownerId)
                            name.set(ownerName)
                            email.set(ownerMail)
                        }
                    }
                    scm {
                        connection.set("scm:git:git://$distribution.git")
                        developerConnection.set("scm:git:ssh://$distribution.git")
                        url.set("https://$distribution")
                    }
                }
            }
        }
    }

    apply(plugin = "signing")
    extensions.getByType(SigningExtension::class.java).apply {
        isRequired = false
        sign(extensions.getByType(PublishingExtension::class.java).publications)
    }
}
