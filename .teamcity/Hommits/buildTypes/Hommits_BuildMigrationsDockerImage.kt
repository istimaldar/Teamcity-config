package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dockerCommand
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_BuildMigrationsDockerImage : Template({
    uuid = "9ea583e2-0ef8-450a-bec2-0d805c773355"
    name = "Build migrations docker image"

    buildNumberPattern = "%build.vcs.number%"

    params {
        param("tag.name", "")
        param("docker.password", """"${'$'}53?ng:D+)&97+gK:eR""")
        param("docker.login", "centaurea")
    }

    vcs {
        cleanCheckout = true
    }

    steps {
        script {
            name = "Execute pre-build image steps"
            id = "RUNNER_14"
            scriptContent = """
                #!/usr/bin/env bash
                
                ls
                
                shopt -s globstar
                
                chmod +x **/*.sh
                
                docker login -u '%docker.login%' -p '%docker.password%'
                
                mkdir %artefact.path%
            """.trimIndent()
        }
        step {
            name = "Extract git info"
            id = "RUNNER_23"
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
            id = "RUNNER_24"
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
            name = "Copy secrets."
            id = "RUNNER_85"
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
        dockerCommand {
            name = "Build code image"
            id = "RUNNER_15"
            commandType = build {
                source = path {
                    path = "./Dockerfile"
                }
                namesAndTags = "%registry.external_address%/hommits-source:%tag.name%"
                commandArgs = "--pull"
            }
        }
        dockerCommand {
            name = "Tag latest image"
            id = "RUNNER_25"
            commandType = other {
                subCommand = "tag"
                commandArgs = """
                    %registry.external_address%/hommits-source:%tag.name%
                    %registry.external_address%/hommits-source:latest
                """.trimIndent()
            }
        }
        dockerCommand {
            name = "Push docker image"
            id = "RUNNER_16"
            commandType = push {
                namesAndTags = "%registry.external_address%/hommits-source:%tag.name%"
            }
        }
        dockerCommand {
            name = "Push latest image"
            id = "RUNNER_26"
            commandType = push {
                namesAndTags = "%registry.external_address%/hommits-source:latest"
            }
        }
    }

    failureConditions {
        executionTimeoutMin = 10
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_10"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    dependencies {
        artifacts(HommitsCiScripts.buildTypes.HommitsCiScripts_Build) {
            id = "ARTIFACT_DEPENDENCY_4"
            buildRule = lastSuccessful()
            artifactRules = """
                containers/code/Dockerfile
                python/extract-git-info.py
                python/generate-tag.py
                python/apply-migrations.py
                python/copy-secrets.py
            """.trimIndent()
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_7")
    }
})
