plugins {
    `kotlin-dsl`
    id("me.omico.gradm") version "3.3.2"
}

repositories {
    mavenCentral()
}

gradm {
    pluginId = "ijsh.gradm"
    debug = true
}
