import me.omico.gradm.addDeclaredRepositories

addDeclaredRepositories()

plugins {
    id("ijsh.gradm")
    id("ijsh.gradle-enterprise")
}

includeBuild("build-logic/project")
