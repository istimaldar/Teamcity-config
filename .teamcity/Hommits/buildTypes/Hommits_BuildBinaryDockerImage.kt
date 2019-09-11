package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_BuildBinaryDockerImage : Template({
    uuid = "7272fd61-1782-476c-b874-de596d0665b2"
    name = "Build binary docker image"

    buildNumberPattern = "%build.vcs.number%"

    params {
        param("tag.name", "")
        param("docker.password", """"${'$'}53?ng:D+)&97+gK:eR""")
        param("docker.login", "centaurea")
    }

    steps {
        script {
            name = "Adjust access rights"
            id = "RUNNER_13"
            scriptContent = """
                #!/usr/bin/env bash
                
                shopt -s globstar
                
                chmod +x **/*.sh
                
                docker login -u '%docker.login%' -p '%docker.password%'
            """.trimIndent()
        }
        step {
            name = "Extract git info"
            id = "RUNNER_63"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --repo-location=.
                --output=git_info.json
            """.trimIndent())
            param("python-script-file-name", "extract-git-info.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        step {
            name = "Obtain tag name"
            id = "RUNNER_18"
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
        step {
            name = "Template dockerfile"
            id = "RUNNER_92"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --template-location=templates/Dockerfile
                -v=development=%development%
                --output-location=Dockerfile
            """.trimIndent())
            param("python-script-file-name", "template-config.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        dockerCommand {
            name = "Build image"
            id = "RUNNER_10"
            commandType = build {
                source = path {
                    path = "./Dockerfile"
                }
                namesAndTags = "%registry.external_address%/hommits:%tag.name%"
                commandArgs = """
                    --pull
                    --build-arg NUGET_LOCATION=%location.nuget%
                    --build-arg STATIC_LOCATION=%location.static%
                    --build-arg DLL_LOCATION=%location.dlls%
                    --build-arg RESOURCES_LOCATION=%location.resources%
                """.trimIndent()
            }
            param("dockerImage.platform", "linux")
        }
        dockerCommand {
            name = "Tag latest image"
            id = "RUNNER_21"
            commandType = other {
                subCommand = "tag"
                commandArgs = """
                    %registry.external_address%/hommits:%tag.name%
                    %registry.external_address%/hommits:latest
                """.trimIndent()
            }
        }
        dockerCommand {
            name = "Push image"
            id = "RUNNER_12"
            commandType = push {
                namesAndTags = "%registry.external_address%/hommits:%tag.name%"
            }
        }
        dockerCommand {
            name = "Push latest image"
            id = "RUNNER_22"
            commandType = push {
                namesAndTags = "%registry.external_address%/hommits:latest"
            }
        }
    }

    failureConditions {
        executionTimeoutMin = 7
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_7"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    dependencies {
        artifacts(HommitsCiScripts.buildTypes.HommitsCiScripts_Build) {
            id = "ARTIFACT_DEPENDENCY_3"
            buildRule = lastSuccessful()
            artifactRules = """
                containers/main/Dockerfile => templates/
                containers/main/configure.sh
                python/generate-tag.py
                python/template-config.py
                python/extract-git-info.py
                python/copy-secrets.py
            """.trimIndent()
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_4")
    }
})
