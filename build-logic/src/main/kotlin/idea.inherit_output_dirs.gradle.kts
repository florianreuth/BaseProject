// https://github.com/florianreuth/BaseProject

plugins {
    id("idea")
}

// When IntelliJ builds instead of Gradle, classes and resources share one output dir, so Fabric Loader sees a single mod
idea {
    module {
        inheritOutputDirs = true
    }
}
