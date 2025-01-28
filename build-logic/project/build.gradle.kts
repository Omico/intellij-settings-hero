plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(consensusGradlePlugins)
    implementation(gradmGeneratedJar)
    implementation(intellijPlatformGradlePlugin)
    implementation(kotlinGradlePlugin)
}
