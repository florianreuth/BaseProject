plugins {
    `kotlin-dsl`
}

dependencies {
    // Only needed by the Fabric and Jar-in-Jar snippets
    implementation(libs.fabric.loom.plugin)
    // Only needed by base.fabric_kotlin
    implementation(libs.kotlin.jvm.plugin)
    // Only needed by base.settings
    implementation(libs.foojay.resolver.convention.plugin)
    // Only needed by extra.fill_build_constants
    implementation(libs.classtokenreplacer.plugin)
}
