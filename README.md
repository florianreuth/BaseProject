# BaseProject
A Gradle convention plugin for streamlined project setup and publishing.

## Basic Setup
The functions provided by this plugin read their configuration from properties in your `gradle.properties` file. Set the
properties for the features you use. Required properties fail the build when they are missing — `setupProject()` requires
`project_group`, `project_version`, `project_description`, and `jvm_version`. Optional properties are simply skipped when
absent.

### Kotlin DSL Example
Add the plugin to your **`settings.gradle.kts`** file:

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()
    }

    plugins {
        id("de.florianreuth.baseproject") version "<version>"
    }
}
```

Update the **`build.gradle.kts`** file:

```kotlin
import de.florianreuth.baseproject.*

plugins {
    id("de.florianreuth.baseproject")
}

// Sets up common configurations: project metadata, repositories, Java toolchain, and compiler options
setupProject()
```

Set project properties in the **`gradle.properties`** file:

```properties
# Required by setupProject()
jvm_version=17
project_group=com.example
project_version=1.0.0-SNAPSHOT
project_description=Example Java project.
# Optional; sets the archive base name of the root project.
# Defaults to the project name from settings.gradle.kts, but is required by setupPublishing() / setupViaPublishing().
project_name=ExampleProject
```

## Publishing
`setupPublishing()` configures a Maven publication (with signing) and registers the Reposilite and Sonatype (Maven
Central) repositories. A repository is only activated when its credentials are present, so you can configure just the ones
you need. GitHub is not a publishing target — the account and repository only supply the POM metadata (project URL, SCM
entries, and license URL).

### Kotlin DSL Example
Add the following to your **`build.gradle.kts`** (below the `setupProject` call):

```kotlin
import de.florianreuth.baseproject.setupPublishing

// Configures the publication, signing, and the Reposilite / Sonatype repositories
setupPublishing()
```

> Publishing to the ViaVersion repository instead? Use `setupViaPublishing()`, which registers the Via repository
> (credentials: `ViaUsername` / `ViaPassword`), derives the metadata from `github.com/ViaVersion/<project_name>`, and sets
> the license to GPL-3.0. It does not register the Reposilite or Sonatype repositories.

### Publishing Metadata

Set the following in your project's **`gradle.properties`**:

```properties
# Required
project_name=ExampleProject
publish_owner_id=florianreuth
publish_owner_name=<full name>
publish_owner_mail=<contact mail>
# Optional; defaults to Apache-2.0
# publish_license=Apache-2.0
# publish_license_url=https://www.apache.org/licenses/LICENSE-2.0
```

`publish_owner_id` is used as the GitHub account and `project_name` as the GitHub repository name; the distribution URL,
SCM entries, and license URL are derived from those automatically. `project_name` also becomes the POM name, while the
published artifact id stays the Gradle project name.

### Signing and Publishing Credentials

Add credentials to the **`gradle.properties`** in your user `.gradle` folder (keep them out of the repository). Only the
repositories whose credentials are present get activated:

```properties
# Signing (publications are signed when these are present)
signing.keyId=<the last 8 digits of your key id>
signing.password=<your key password>
signing.secretKeyRingFile=<path to your keyring file>
# Sonatype / Maven Central
sonatypeToken=<your Sonatype token>
sonatypePassword=<your Sonatype token password>
# Reposilite
reposiliteUsername=<your Reposilite username>
reposilitePassword=<your Reposilite password>
```

## Fabric Setup
`setupFabric()` applies Fabric Loom, wires up the Fabric loader and Minecraft dependencies, expands `fabric.mod.json`,
excludes the `run/` folder from the IntelliJ IDEA model, and — if a `<project-name>.accesswidener` file (the lower-cased
Gradle project name) is present under `src/main/resources` — loads it automatically.

### Kotlin DSL Example
Add the following to your **`build.gradle.kts`** (below the `setupProject` call):

```kotlin
import de.florianreuth.baseproject.integration.setupFabric

setupFabric()
```

Set the required versions in **`gradle.properties`**:

```properties
# Required
minecraft_version=1.21.5
fabric_loader_version=0.16.14
# Required once the Kotlin plugin is applied; the language adapter dependency is then added automatically
# fabric_kotlin_version=1.13.1+kotlin.2.1.20
# Optional
# fabric_api_version=0.119.2+1.21.5   (exposed as the `fabricApiVersion` property; no dependency is added for it)
# supported_minecraft_versions=1.21.4,1.21.5
```

## Moving On
The plugin ships additional utilities. A couple of the common ones:

### Shaded dependencies
Embed dependencies directly into the output JAR:

```kotlin
import de.florianreuth.baseproject.core.configureShadedDependencies

val library = configureShadedDependencies()

dependencies {
    library("group:artifact:version")
}
```

### Application JAR
Set the `Main-Class` manifest attribute from the `application_main` property:

```kotlin
import de.florianreuth.baseproject.core.configureApplication

configureApplication()
```

```properties
application_main=com.example.Main
```

> `configureApplication()` no longer excludes the `run/` folder from the IntelliJ IDEA model. If you want that, call
`excludeRunFolder()` (from `de.florianreuth.baseproject.integration`) explicitly.

For more utilities and detailed documentation, please refer to the Kotlin files and methods in the plugin, which include
detailed KotlinDoc comments.

## Contact

- Issues: https://github.com/florianreuth/BaseProject/issues
- Discord: https://florianreuth.de/discord
