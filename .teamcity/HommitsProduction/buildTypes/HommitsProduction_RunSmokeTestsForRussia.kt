package HommitsProduction.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.finishBuildTrigger

object HommitsProduction_RunSmokeTestsForRussia : BuildType({
    templates(Hommits.buildTypes.Hommits_RunSmokeTests)
    uuid = "22e9fae1-4355-42ec-9bdd-2808264a9a11"
    name = "Run smoke tests for russia"

    params {
        param("production.url", "https://rus.hommits.istimaldar.xyz")
        param("tenant", "russia")
    }

    vcs {
        root(HommitsProduction.vcsRoots.HommitsProductionVcsRoot)
    }

    triggers {
        finishBuildTrigger {
            id = "TRIGGER_4"
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
