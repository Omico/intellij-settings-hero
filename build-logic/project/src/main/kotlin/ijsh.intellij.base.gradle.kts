plugins {
    id("org.jetbrains.intellij.platform.base")
}

dependencies {
    intellijPlatform {
        val type = providers.gradleProperty("project.intellij.platformType")
        val version = providers.gradleProperty("project.intellij.platformMinVersion")
        create(type, version)
        instrumentationTools()
        pluginVerifier()
        zipSigner()
    }
}
