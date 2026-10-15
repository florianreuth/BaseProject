// https://github.com/florianreuth/BaseProject

plugins {
    id("configuration.jar_in_jar")
}

val jarInJar = configurations.named("jarInJar")
listOf("api", "implementation", "include").forEach { targetName ->
    configurations.named(targetName) {
        defaultDependencies {
            jarInJar.get().incoming.resolutionResult.allComponents
                .filterNot { component ->
                    component.variants.any {
                        it.isPlatform()
                    }
                }
                .mapNotNull {
                    it.id as? ModuleComponentIdentifier
                }
                .forEach { id ->
                    add(project.dependencies.create("${id.group}:${id.module}:${id.version}") {
                        isTransitive = false
                    })
                }
        }
    }
}

fun ResolvedVariantResult.isPlatform(): Boolean {
    val category = attributes.keySet().firstOrNull {
        it.name == Category.CATEGORY_ATTRIBUTE.name
    } ?: return false
    return attributes.getAttribute(category).toString() in setOf(Category.REGULAR_PLATFORM, Category.ENFORCED_PLATFORM)
}
