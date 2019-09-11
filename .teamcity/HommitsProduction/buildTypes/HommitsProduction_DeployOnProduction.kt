package HommitsProduction.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object HommitsProduction_DeployOnProduction : BuildType({
    templates(Hommits.buildTypes.Hommits_DeployOnProduction)
    uuid = "a397edf7-545f-4234-a659-ae521c22fc03"
    name = "Deploy on production"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    maxRunningBuilds = 1

    params {
        param("tag.name", "")
    }

    vcs {
        root(HommitsProduction.vcsRoots.HommitsProductionVcsRoot)
    }

    steps {
        script {
            name = "Scale down deployment"
            id = "RUNNER_97"
            enabled = false
            scriptContent = "kubectl scale deployment -n hommits-production hommits --replicas=1"
        }
        script {
            name = "Scale UP deployment"
            id = "RUNNER_98"
            enabled = false
            scriptContent = "kubectl scale deployment -n hommits-production hommits --replicas=2"
        }
        stepsOrder = arrayListOf("RUNNER_9", "RUNNER_45", "RUNNER_51", "RUNNER_33", "RUNNER_76", "RUNNER_77", "RUNNER_32", "RUNNER_82", "RUNNER_34", "RUNNER_35", "RUNNER_97", "RUNNER_73", "RUNNER_74", "RUNNER_98", "RUNNER_56", "RUNNER_57", "RUNNER_81", "RUNNER_83")
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_13"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:ec799d70-98ee-4178-af10-506569e486df"
            }
        }
    }

    dependencies {
        snapshot(HommitsProduction_BuildBinaryDockerImage_2) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
        snapshot(HommitsProduction_BuildGenerationDockerImage) {
        }
        snapshot(HommitsProduction_BuildMigrationDockerImage) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }
})
