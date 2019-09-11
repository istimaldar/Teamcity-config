package HommitsProduction.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.finishBuildTrigger

object HommitsProduction_RunSmokeTestsForNewzealand : BuildType({
    templates(Hommits.buildTypes.Hommits_RunSmokeTests)
    uuid = "06f54985-0050-41f8-ab55-0f5c545829a6"
    name = "Run smoke tests for newzealand"

    params {
        param("production.url", "https://nz.hommits.istimaldar.xyz")
        param("tenant", "newzealand")
    }

    vcs {
        root(HommitsProduction.vcsRoots.HommitsProductionVcsRoot)
    }

    triggers {
        finishBuildTrigger {
            id = "TRIGGER_5"
            buildType = "${HommitsProduction_DeployOnProduction.id}"
            successfulOnly = true
        }
    }

    dependencies {
        snapshot(HommitsProduction_DeployOnProduction) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }
})
