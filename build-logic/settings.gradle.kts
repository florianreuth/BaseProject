dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        // Only needed by the Fabric and Jar-in-Jar snippets
        maven("https://maven.fabricmc.net/")
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
