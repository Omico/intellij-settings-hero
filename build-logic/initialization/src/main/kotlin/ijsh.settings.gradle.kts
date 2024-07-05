import me.omico.gradm.addDeclaredRepositories

addDeclaredRepositories()

plugins {
    id("ijsh.develocity")
    id("ijsh.gradm")
}

includeBuild("build-logic/project")
