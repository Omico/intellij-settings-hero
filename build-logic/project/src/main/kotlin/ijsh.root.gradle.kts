import me.omico.consensus.dsl.requireRootProject

plugins {
    id("ijsh.gradm")
    id("ijsh.root.git")
    id("ijsh.root.spotless")
}

requireRootProject()

val wrapper: Wrapper by tasks.named<Wrapper>("wrapper") {
    gradleVersion = versions.gradle
    distributionType = Wrapper.DistributionType.BIN
}
