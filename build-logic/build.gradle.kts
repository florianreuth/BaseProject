plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    // Only needed by the base.fabric-* snippets
    maven("https://maven.fabricmc.net/")
}

dependencies {
    // Only needed by the base.fabric-* snippets
    implementation(libs.fabric.loom.plugin)
    // Only needed by base.fabric-kotlin-conventions
    implementation(libs.kotlin.jvm.plugin)
    // Only needed by base.settings-conventions
    implementation(libs.foojay.resolver.convention.plugin)
    // Only needed by via.build-constants
    implementation(libs.classtokenreplacer.plugin)
}
