package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_DeployOnStaging : Template({
    uuid = "959fb7a3-1bb4-4c25-a81d-da2aa5ee1023"
    name = "Deploy on staging"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    buildNumberPattern = "%build.vcs.number%"
    maxRunningBuilds = 1

    steps {
        step {
            name = "Extract git info"
            id = "RUNNER_66"
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
            id = "RUNNER_67"
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
            name = "Template kubernetes config"
            id = "RUNNER_68"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --template-location=templates/staging.yaml
                -v=tag=%tag.name%
                -v=registry=%registry.external_address%
                --output-location=staging.yaml
            """.trimIndent())
            param("python-script-file-name", "template-config.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        step {
            name = "Template generation config"
            id = "RUNNER_78"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --template-location=templates/staging.generation.yaml
                -v=tag=%tag.name%
                -v=registry=%registry.external_address%
                --output-location=staging.generation.yaml
            """.trimIndent())
            param("python-script-file-name", "template-config.py")
            param("python-script-code", """
                --template-location=templates/production.generation.yaml
                -v=tag=%tag.name%
                -v=registry=%registry.external_address%
                --output-location=production.generation.yaml
            """.trimIndent())
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        step {
            name = "Template dashboard config"
            id = "RUNNER_86"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --template-location=templates/staging.dashboard.yaml
                -v=tag=%tag.name%
                -v=registry=%registry.external_address%
                --output-location=staging.dashboard.yaml
            """.trimIndent())
            param("python-script-file-name", "template-config.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        script {
            name = "Remove all existing jobs"
            id = "RUNNER_84"
            scriptContent = "kubectl delete jobs --namespace=hommits-staging ${'$'}(kubectl get jobs --namespace=hommits-staging -o jsonpath='{.items[*].metadata.name}') || :"
        }
        script {
            name = "Apply kubernetes config"
            id = "RUNNER_20"
            scriptContent = "kubectl apply -f staging.yaml"
        }
        step {
            name = "Wait for kubernetes configuration to be applied"
            id = "RUNNER_70"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", "staging.yaml")
            param("python-script-file-name", "wait_for_deployment.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        script {
            name = "Apply generation"
            id = "RUNNER_79"
            scriptContent = "kubectl apply -f staging.generation.yaml"
        }
        script {
            name = "Wait for generation to finish"
            id = "RUNNER_80"
            scriptContent = "kubectl wait --for=condition=complete --timeout=1200s --namespace=hommits-staging job/hommits-generate"
        }
        script {
            name = "Apply dashboard config"
            id = "RUNNER_87"
            scriptContent = "kubectl apply -f staging.dashboard.yaml"
        }
        step {
            name = "Wait for dashboard configuration to be applied"
            id = "RUNNER_88"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", "staging.dashboard.yaml")
            param("python-script-file-name", "wait_for_deployment.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
    }

    failureConditions {
        executionTimeoutMin = 25
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_9"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
        feature {
            id = "BUILD_EXT_20"
            type = "JetBrains.SharedResources"
            param("locks-param", "Deploy writeLock")
        }
    }

    dependencies {
        artifacts(HommitsCiScripts.buildTypes.HommitsCiScripts_Build) {
            id = "ARTIFACT_DEPENDENCY_12"
            buildRule = lastSuccessful()
            artifactRules = """
                python/extract-git-info.py
                python/generate-tag.py
                python/template-config.py
                python/kubernetes/staging.yaml => templates/
                python/kubernetes/staging.generation.yaml => templates/
                python/kubernetes/staging.dashboard.yaml => templates/
                python/wait_for_deployment.py
            """.trimIndent()
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_11")
    }
})
