package HommitsCiScripts.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object HommitsCiScripts_Build : BuildType({
    uuid = "f06f2b6d-3bc8-4285-b0e3-f3e3af4f502c"
    name = "Build"

    artifactRules = """
        **/* => /
        -: .git/**/* => /
        -: .gitignore => /
    """.trimIndent()
    buildNumberPattern = "%build.vcs.number%"

    vcs {
        root(HommitsCiScripts.vcsRoots.HommitsCiScriptsVcsRoot)
    }

    triggers {
        vcs {
        }
    }

    requirements {
        doesNotEqual("machineType", "local")
    }
})
