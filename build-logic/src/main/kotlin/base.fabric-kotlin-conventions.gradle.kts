plugins {
    id("base.fabric-conventions")
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(the<VersionCatalogsExtension>().named("libs").findLibrary("fabric-language-kotlin").get())
}

kotlin {
    compilerOptions.freeCompilerArgs.add("-Xjsr305=ignore")
}
