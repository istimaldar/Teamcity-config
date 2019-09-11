package HommitsProduction.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object HommitsProduction_BuildMigrationDockerImage : BuildType({
    templates(Hommits.buildTypes.Hommits_BuildMigrationsDockerImage)
    uuid = "2b7bf224-e767-47a2-8131-dffc1995a7a5"
    name = "Build migration docker image"

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
