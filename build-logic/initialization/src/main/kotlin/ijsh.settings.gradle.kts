import me.omico.gradle.initialization.includeAllSubprojectModules
import me.omico.gradm.addDeclaredRepositories

addDeclaredRepositories()

plugins {
    id("ijsh.develocity")
    id("ijsh.gradm")
    id("ijsh.intellij")
}

includeBuild("build-logic/project")

includeAllSubprojectModules("ijsh")
