plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(com.diffplug.spotless)
    implementation(consensusGradlePlugins)
    implementation(gradmGeneratedJar)
    implementation(intellijPlatformGradlePlugin)
    implementation(kotlinGradlePlugin)
}
