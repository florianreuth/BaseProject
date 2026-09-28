plugins {
    id("base.fabric-jar-in-jar")
}

val jarInJar = configurations.named("jarInJar")
listOf("api", "implementation", "include").forEach { targetName ->
    configurations.named(targetName) {
        defaultDependencies {
            jarInJar.get().incoming.resolutionResult.allComponents
                .mapNotNull { it.id as? ModuleComponentIdentifier }
                .forEach { id ->
                    add(project.dependencies.create("${id.group}:${id.module}:${id.version}") {
                        isTransitive = false
                    })
                }
        }
    }
}
