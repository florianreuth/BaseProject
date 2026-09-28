plugins {
    id("base.fabric")
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(project.the<VersionCatalogsExtension>().named("libs").findLibrary("fabric-language-kotlin").get())
}

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xjsr305=ignore")
}
