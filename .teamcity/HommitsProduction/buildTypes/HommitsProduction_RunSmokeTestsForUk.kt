package HommitsProduction.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.finishBuildTrigger

object HommitsProduction_RunSmokeTestsForUk : BuildType({
    templates(Hommits.buildTypes.Hommits_RunSmokeTests)
    uuid = "343c30eb-b7aa-4dc4-8ddc-2a9263862d8f"
    name = "Run smoke tests for uk"

    params {
        param("production.url", "https://uk.hommits.istimaldar.xyz")
        param("tenant", "uk")
    }

    vcs {
        root(HommitsProduction.vcsRoots.HommitsProductionVcsRoot)
    }

    triggers {
        finishBuildTrigger {
            id = "TRIGGER_3"
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
