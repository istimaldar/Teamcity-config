package HommitsProduction.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.finishBuildTrigger

object HommitsProduction_RunSmokeTestsForBelarus : BuildType({
    templates(Hommits.buildTypes.Hommits_RunSmokeTests)
    uuid = "1747e9df-2b1e-4c81-8b2c-0e40a49308a9"
    name = "Run smoke tests for belarus"

    params {
        param("production.url", "https://bel.hommits.istimaldar.xyz")
        param("tenant", "belarus")
    }

    vcs {
        root(HommitsProduction.vcsRoots.HommitsProductionVcsRoot)
    }

    triggers {
        finishBuildTrigger {
            id = "TRIGGER_6"
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
