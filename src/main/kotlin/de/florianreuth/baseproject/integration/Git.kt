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
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/**
 * Provides information about the latest commit in the Git repository.
 */
fun Project.latestCommitHash(): String {
    return runGitCommand(listOf("rev-parse", "--short", "HEAD"))
}

/**
 * Provides the commit message of the latest commit in the Git repository.
 */
fun Project.latestCommitMessage(): String {
    return runGitCommand(listOf("log", "-1", "--pretty=%B"))
}

/**
 * Provides the name of the current Git branch.
 */
fun Project.branchName(): String {
    return runGitCommand(listOf("rev-parse", "--abbrev-ref", "HEAD"))
}

/**
 * Runs a Git command and returns the output. Returns "unknown" if the command fails.
 */
fun Project.runGitCommand(args: List<String>): String {
    return providers.of(GitCommand::class.java) { parameters.args.set(args) }.getOrNull() ?: "unknown"
}

/**
 * A Gradle ValueSource that runs a Git command and returns its output.
 * If the command fails, the ValueSource returns null.
 */
abstract class GitCommand : ValueSource<String, GitCommand.GitCommandParameters> {

    @get:Inject
    abstract val execOperations: ExecOperations

    interface GitCommandParameters : ValueSourceParameters {
        val args: ListProperty<String>
    }

    override fun obtain(): String? {
        try {
            val command = listOf("git") + parameters.args.get()
            val output = ByteArrayOutputStream()
            execOperations.exec {
                commandLine = command
                standardOutput = output
                isIgnoreExitValue = true
            }

            return output.toString(Charsets.UTF_8).trim().takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            return null
        }
    }
}
