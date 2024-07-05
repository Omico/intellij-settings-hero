import me.omico.consensus.dsl.requireRootProject

plugins {
    id("ijsh.gradm")
    id("ijsh.root.git")
    id("ijsh.root.spotless")
}

requireRootProject()

consensus {
    allprojects {
        group = gradleProperty("project.group")
        version = gradleProperty("project.version")
    }
}

val wrapper: Wrapper by tasks.named<Wrapper>("wrapper") {
    gradleVersion = versions.gradle
    distributionType = Wrapper.DistributionType.BIN
}
