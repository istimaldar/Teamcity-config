package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_BuildGenerationDockerImage : Template({
    uuid = "b3648ca9-8f73-4493-95d4-ee21beba4f80"
    name = "Build generation docker image"

    buildNumberPattern = "%build.vcs.number%"

    params {
        param("tag.name", "")
        param("docker.password", """"${'$'}53?ng:D+)&97+gK:eR""")
        param("docker.login", "centaurea")
    }

    steps {
        script {
            name = "Execute pre-build image steps"
            id = "RUNNER_54"
            scriptContent = """
                #!/usr/bin/env bash
                
                ls
                
                shopt -s globstar
                
                chmod +x **/*.sh
                
                docker login -u '%docker.login%' -p '%docker.password%'
                
                mkdir -p %artefact.path%
            """.trimIndent()
        }
        step {
            name = "Extract git info"
            id = "RUNNER_55"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", "--output=git_info.json")
            param("python-script-file-name", "extract-git-info.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        step {
            name = "Obtain tag name"
            id = "RUNNER_60"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --json-location=git_info.json
                --output-template=##teamcity[setParameter name='tag.name' value='{{ tag }}']
            """.trimIndent())
            param("python-script-file-name", "generate-tag.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        dockerCommand {
            name = "Build generation image"
            id = "RUNNER_61"
            commandType = build {
                source = path {
                    path = "./Dockerfile"
                }
                namesAndTags = "%registry.external_address%/hommits-generate:%tag.name%"
                commandArgs = "--pull"
            }
        }
        dockerCommand {
            name = "Tag latest image"
            id = "RUNNER_62"
            commandType = other {
                subCommand = "tag"
                commandArgs = """
                    %registry.external_address%/hommits-generate:%tag.name%
                    %registry.external_address%/hommits-generate:latest
                """.trimIndent()
            }
        }
        dockerCommand {
            name = "Push docker image"
            id = "RUNNER_64"
            commandType = push {
                namesAndTags = "%registry.external_address%/hommits-generate:%tag.name%"
            }
        }
        dockerCommand {
            name = "Push latest image"
            id = "RUNNER_69"
            commandType = push {
                namesAndTags = "%registry.external_address%/hommits-generate:latest"
            }
        }
    }

    failureConditions {
        executionTimeoutMin = 10
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_6"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    dependencies {
        artifacts(HommitsCiScripts.buildTypes.HommitsCiScripts_Build) {
            id = "ARTIFACT_DEPENDENCY_9"
            buildRule = lastSuccessful()
            artifactRules = """
                containers/generation/Dockerfile
                containers/generation/startup.sh
                python/extract-git-info.py
                python/generate-tag.py
            """.trimIndent()
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_6")
    }
})
