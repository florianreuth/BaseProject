# BaseProject
Gradle Kotlin DSL build-logic snippets for streamlined project setup and publishing.

Every project keeps its own `build-logic` included build:

copy `build-logic/build.gradle.kts`, `build-logic/settings.gradle.kts` and the snippets you need from [`build-logic/src/main/kotlin`](build-logic/src/main/kotlin).

The plugin versions of `build-logic` come from your `gradle/libs.versions.toml`, see
[`gradle/libs.versions.toml`](gradle/libs.versions.toml).

## Setup
**`settings.gradle.kts`**

```kotlin
pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("base.settings")
}

rootProject.name = "example"
```

**`build.gradle.kts`**

```kotlin
plugins {
    id("base.java")
    id("base.maven_publish")
    id("publishing.reposilite")
}
```

**`gradle.properties`**

```properties
jvm_version=21
project_group=com.example
project_name=Example
project_version=1.0.0-SNAPSHOT
project_description=Example project.
# Required by the publishing snippets
publish_owner_id=florianreuth
publish_owner_name=<full name>
publish_owner_mail=<contact mail>
```

## Fabric
Keep the Fabric repository in `pluginManagement`, since the build-logic dependencies are resolved through it, apply
`base.fabric_settings` so Loom declares its repositories in the settings, and declare the versions in your catalog:

```kotlin
pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("base.settings")
    id("base.fabric_settings")
}
```

```toml
[libraries]
minecraft = { module = "com.mojang:minecraft", version.ref = "minecraft" }
fabric-loader = { module = "net.fabricmc:fabric-loader", version.ref = "fabric-loader" }
fabric-loom-plugin = { module = "net.fabricmc.fabric-loom:net.fabricmc.fabric-loom.gradle.plugin", version.ref = "fabric-loom" }
```

## Credentials
Keep them in the `gradle.properties` of your user `.gradle` folder: `signing.keyId`, `signing.password`,
`signing.secretKeyRingFile`, `reposiliteUsername` / `reposilitePassword`, `sonatypeToken` / `sonatypePassword` and
`ViaUsername` / `ViaPassword`.

## Contact

- Issues: https://github.com/florianreuth/BaseProject/issues
- Discord: https://florianreuth.de/discord
