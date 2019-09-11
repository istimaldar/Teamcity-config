package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_BuildBackend : Template({
    uuid = "5ba85e06-5781-40a2-a283-05988d148554"
    name = "Build backend"

    buildNumberPattern = "%build.vcs.number%"

    steps {
        script {
            name = "CRUNCH! Generate config file."
            id = "RUNNER_29"
            workingDir = "src/Web"
            scriptContent = "echo '{}' > config.local.json"
        }
        dotnetBuild {
            name = "Build backend"
            id = "RUNNER_30"
            projects = "%projects%"
            configuration = "Release"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
    }

    failureConditions {
        executionTimeoutMin = 7
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_1"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_3")
    }
})
