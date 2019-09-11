package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_TestBackend : Template({
    uuid = "7a0b6f48-0743-4cf2-9309-816c18f05c38"
    name = "Test backend"

    artifactRules = "tests/Test/coverage/* => ./"
    buildNumberPattern = "%build.vcs.number%"

    steps {
        script {
            name = "CRUNCH! Generate config file."
            id = "RUNNER_41"
            scriptContent = "echo '{}' > src/Web/config.local.json"
        }
        dotnetTest {
            name = "Run unit tests"
            id = "RUNNER_38"
            projects = "Test.csproj"
            workingDir = "tests/Test"
            args = """
                --filter Category!=DeprecatedTest
                /p:CollectCoverage=true /p:CoverletOutputFormat=opencover
                /p:CoverletOutput=coverage/
            """.trimIndent()
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }

    failureConditions {
        executionTimeoutMin = 25
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_4"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_20")
    }
})
