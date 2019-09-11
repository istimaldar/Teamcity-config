package HommitsProduction.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.finishBuildTrigger

object HommitsProduction_RunSmokeTestsForUsa : BuildType({
    templates(Hommits.buildTypes.Hommits_RunSmokeTests)
    uuid = "323d0876-4d53-4fcd-a622-5a96647086f7"
    name = "Run smoke tests for usa"

    params {
        param("production.url", "https://usa.hommits.istimaldar.xyz")
        param("tenant", "usa")
    }

    vcs {
        root(HommitsProduction.vcsRoots.HommitsProductionVcsRoot)
    }

    triggers {
        finishBuildTrigger {
            id = "TRIGGER_7"
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
