package HommitsCiScripts

import HommitsCiScripts.buildTypes.*
import HommitsCiScripts.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.Project

object Project : Project({
    uuid = "e0660a24-5675-453f-8745-63c6f0d914eb"
    id("HommitsCiScripts")
    parentId("Hommits")
    name = "Scripts"

    vcsRoot(HommitsCiScriptsVcsRoot)

    buildType(HommitsCiScripts_Build)
})
