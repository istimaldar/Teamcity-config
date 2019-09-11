package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_TestFrontend : Template({
    uuid = "ac369667-d7d8-4801-9d88-1792ef08a95e"
    name = "Test frontend"

    artifactRules = "tests/Test/coverage/* => ./"
    buildNumberPattern = "%build.vcs.number%"

    steps {
        script {
            name = "Restore npm dependency"
            id = "RUNNER_31"
            workingDir = "tests/Test"
            scriptContent = "npm install"
        }
        script {
            name = "Run js test"
            id = "RUNNER_37"
            workingDir = "tests/Test"
            scriptContent = "npm run test"
        }
    }

    failureConditions {
        executionTimeoutMin = 10
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_3"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_21")
    }
})
