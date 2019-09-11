package HommitsProduction.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object HommitsProduction_BuildGenerationDockerImage : BuildType({
    templates(Hommits.buildTypes.Hommits_BuildGenerationDockerImage)
    uuid = "dc605ae6-e323-4421-80b7-7c9584b7accc"
    name = "Build generation docker image"

    vcs {
        root(HommitsProduction.vcsRoots.HommitsProductionVcsRoot)
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            triggerRules = "+:**"
        }
    }
})
