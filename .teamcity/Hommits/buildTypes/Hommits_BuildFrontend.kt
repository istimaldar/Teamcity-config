package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_BuildFrontend : Template({
    uuid = "95d8991d-ccc2-4f8f-88d7-5d5c9007e8c5"
    name = "Build frontend"

    buildNumberPattern = "%build.vcs.number%"

    steps {
        script {
            name = "Restore npm dependency"
            id = "RUNNER_11"
            workingDir = "src/Web"
            scriptContent = "npm install"
        }
        script {
            name = "Build npm"
            id = "RUNNER_27"
            workingDir = "src/Web"
            scriptContent = "npm run build-dev"
        }
    }

    failureConditions {
        executionTimeoutMin = 12
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_2"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_5")
    }
})
