package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dotnetPublish
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_PublishProject : Template({
    uuid = "24ceaaeb-11d4-427a-9c3e-e4c2771d95f0"
    name = "Publish project"

    artifactRules = """
        %location.dlls% => release.zip!%location.dlls%
        %location.nuget% => release.zip!%location.nuget%
        %location.static% => release.zip!%location.static%
        %location.resources% => release.zip!%location.resources%
    """.trimIndent()
    buildNumberPattern = "%build.vcs.number%"

    params {
        param("Python.3", "python3.7")
        param("location.publish", "published/")
    }

    vcs {
        cleanCheckout = true
    }

    steps {
        step {
            name = "Copy secrets."
            id = "RUNNER_75"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --project-path=.
                --secrets-path=/srv/secrets
            """.trimIndent())
            param("python-script-file-name", "copy-secrets.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        script {
            name = "Restore npm dependency"
            id = "RUNNER_6"
            workingDir = "src/Web"
            scriptContent = "npm install"
        }
        script {
            name = "Build npm"
            id = "RUNNER_7"
            workingDir = "src/Web"
            scriptContent = "npm run build-verbose"
        }
        script {
            name = "Build email templates"
            id = "RUNNER_50"
            workingDir = "src/Web"
            scriptContent = "npm run build-emails"
        }
        dotnetPublish {
            name = "Publish needed projects"
            id = "RUNNER_8"
            projects = "%projects%"
            configuration = "Release"
            outputDir = "../../%location.publish%"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        script {
            name = "Split layers"
            id = "RUNNER_59"
            scriptContent = "%Python.3% split-layers.py --code-location=. --output-location=%location.publish% --resources-location=%location.resources% --nuget-location=%location.nuget% --dll-location=%location.dlls% --static-location=%location.static% --solution-name=%solution.name%"
        }
        script {
            name = "Copy secret config"
            id = "RUNNER_91"
            scriptContent = "cp secrets.json %location.dlls%"
        }
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_11"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    dependencies {
        artifacts(HommitsCiScripts.buildTypes.HommitsCiScripts_Build) {
            id = "ARTIFACT_DEPENDENCY_5"
            buildRule = lastSuccessful()
            artifactRules = """
                python/split-layers.py => ./
                python/copy-secrets.py => ./
            """.trimIndent()
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_15")
    }
})
