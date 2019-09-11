package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_RunE2eTests : Template({
    uuid = "6cf20fdc-b4d2-4d54-ba2a-aac4a5709552"
    name = "Run E2E tests"

    artifactRules = """
        tests/Test/cypress/**/*.mp4 => ./
        tests/Test/cypress/**/*.png => ./
    """.trimIndent()
    buildNumberPattern = "%build.vcs.number%"

    params {
        param("env.CYPRESS_TESTS_HOST", "%staging.url%")
    }

    steps {
        script {
            name = "Restore npm dependency"
            id = "RUNNER_71"
            workingDir = "tests/Test"
            scriptContent = "npm install"
        }
        script {
            name = "Run cypress tests"
            id = "RUNNER_72"
            workingDir = "tests/Test"
            scriptContent = """
                export CYPRESS_baseUrl=%testServer.url%;
                export CYPRESS_configFile=%tenant%
                npm run cypress:teamcity:run;
            """.trimIndent()
        }
    }

    failureConditions {
        executionTimeoutMin = 15
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_12"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_16")
    }
})
