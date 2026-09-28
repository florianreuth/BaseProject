plugins {
    id("via.viaversion-maven-publishing")
}

// base.publishing-conventions reads these when applied, so it is applied only after they are set
extra["publish_github_account"] = "ViaVersion"
extra["publish_license"] = "GPL-3.0"
apply(plugin = "base.publishing-conventions")
