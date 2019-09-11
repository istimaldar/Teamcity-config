package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_RunSmokeTests : Template({
    uuid = "1ad1acd1-88e1-414b-b60e-ed1311d273ce"
    name = "Run smoke tests"

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
                export CYPRESS_baseUrl=%production.url%;
                export CYPRESS_configFile=%tenant%
                npm run cypress:teamcity:run-smoke;
            """.trimIndent()
        }
    }

    failureConditions {
        executionTimeoutMin = 10
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
        doesNotEqual("machineType", "local", "RQ_17")
    }
})
