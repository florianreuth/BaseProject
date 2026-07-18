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

package de.florianreuth.baseproject.publishing

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.authentication.http.BasicAuthentication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Configures the project to use the Florian Reuth's Reposilite repository.
 * URL: `https://maven.florianreuth.de/`
 */
fun Project.configureReposiliteRepository() {
    val reposiliteUsername = findProperty("reposiliteUsername") as String?
    val reposilitePassword = findProperty("reposilitePassword") as String?
    if (reposiliteUsername == null || reposilitePassword == null) {
        return
    }

    apply(plugin = "maven-publish")
    extensions.getByType(PublishingExtension::class.java).apply {
        repositories.maven {
            name = "reposilite"
            url = uri(
                "https://maven.florianreuth.de/" + if (project.version.toString()
                        .contains("SNAPSHOT")
                ) "snapshots" else "releases"
            )
            credentials {
                username = reposiliteUsername
                password = reposilitePassword
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

/**
 * Configures the project to use the Sonatype (Maven Central) repository.
 * URL: `https://central.sonatype.com/`
 */
fun Project.configureSonatypeRepository() {
    val sonatypeToken = findProperty("sonatypeToken") as String?
    val sonatypePassword = findProperty("sonatypePassword") as String?
    if (sonatypeToken == null || sonatypePassword == null) {
        return
    }

    apply(plugin = "maven-publish")
    extensions.getByType(PublishingExtension::class.java).apply {
        val snapshot = project.version.toString().contains("SNAPSHOT")

        repositories.maven {
            name = "ossrh"
            val releasesUrl = "https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2"
            val snapshotsUrl = "https://central.sonatype.com/repository/maven-snapshots"
            url = uri(if (snapshot) snapshotsUrl else releasesUrl)

            credentials {
                username = sonatypeToken
                password = sonatypePassword
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }

        tasks.withType(PublishToMavenRepository::class.java) {
            if (!name.endsWith("ToOssrhRepository") || snapshot) {
                return@withType
            }

            val mavenGroup = project.group.toString()
            val closeUrl = "https://ossrh-staging-api.central.sonatype.com/manual/upload/defaultRepository/$mavenGroup"

            doLast("closeOssrhRepository") {
                val connection = (URL(closeUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    val encodedAuth =
                        Base64.getEncoder().encodeToString("${sonatypeToken}:${sonatypePassword}".toByteArray())
                    setRequestProperty("Authorization", "Basic $encodedAuth")
                }

                if (connection.responseCode != 200) {
                    throw GradleException(
                        "Failed to close staging repository: ${connection.responseCode} ${connection.responseMessage}"
                    )
                }

                connection.disconnect()
            }
        }
    }
}

/**
 * Configures the project to use the ViaVersion repository.
 * URL: `https://repo.viaversion.com/`
 */
fun Project.configureViaVersionRepository() {
    apply(plugin = "maven-publish")
    extensions.getByType(PublishingExtension::class.java).apply {
        repositories.maven {
            name = "Via"
            url = uri("https://repo.viaversion.com/")
            credentials {
                username = findProperty("ViaUsername") as String?
                password = findProperty("ViaPassword") as String?
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}
