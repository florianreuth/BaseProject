import de.florianreuth.baseproject.latestCommitHash
import net.raphimc.classtokenreplacer.extension.ClassTokenReplacerExtension

plugins {
    java
    id("net.raphimc.class-token-replacer")
}

val implVersion = "git-${property("project_name")}-${project.version}:${latestCommitHash()}"
sourceSets.configureEach {
    extensions.getByType<ClassTokenReplacerExtension>().apply {
        property("\${version}", project.version)
        property("\${impl_version}", implVersion)
    }
}
