package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_DeployOnProduction : Template({
    uuid = "ac999265-3536-428b-8e79-9a0b3de8b2c4"
    name = "Deploy on production"

    buildNumberPattern = "%build.vcs.number%"

    steps {
        step {
            name = "Extract git info"
            id = "RUNNER_9"
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
            id = "RUNNER_45"
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
            id = "RUNNER_51"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --template-location=templates/production.yaml
                -v=tag=%tag.name%
                -v=registry=%registry.external_address%
                -v=development=%development%
                --output-location=production.yaml
            """.trimIndent())
            param("python-script-file-name", "template-config.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        step {
            name = "Template migrations config"
            id = "RUNNER_33"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --template-location=templates/production.migrations.yaml
                -v=tag=%tag.name%
                -v=registry=%registry.external_address%
                --output-location=production.migrations.yaml
            """.trimIndent())
            param("python-script-file-name", "template-config.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        step {
            name = "Template generation config"
            id = "RUNNER_76"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --template-location=templates/production.generation.yaml
                -v=tag=%tag.name%
                -v=registry=%registry.external_address%
                --output-location=production.generation.yaml
            """.trimIndent())
            param("python-script-file-name", "template-config.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        step {
            name = "Template dashboard config"
            id = "RUNNER_77"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --template-location=templates/production.dashboard.yaml
                -v=tag=%tag.name%
                -v=registry=%registry.external_address%
                --output-location=production.dashboard.yaml
            """.trimIndent())
            param("python-script-file-name", "template-config.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        step {
            name = "Obtain tennants information"
            id = "RUNNER_32"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --config-location=src/Site/tenants.map.json
                --output-location=tenants-domains.json
            """.trimIndent())
            param("python-script-file-name", "extract-tenants.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        script {
            name = "Remove all existing jobs"
            id = "RUNNER_82"
            scriptContent = "kubectl delete jobs --namespace=hommits-production ${'$'}(kubectl get jobs --namespace=hommits-production -o jsonpath='{.items[*].metadata.name}') || :"
        }
        script {
            name = "Apply migrations"
            id = "RUNNER_34"
            scriptContent = "kubectl apply -f production.migrations.yaml"
        }
        script {
            name = "Wait for migrations to finish"
            id = "RUNNER_35"
            scriptContent = "kubectl wait --for=condition=complete --timeout=4500s --namespace=hommits-production job/hommits-migrations"
        }
        script {
            name = "Apply generation"
            id = "RUNNER_73"
            scriptContent = "kubectl apply -f production.generation.yaml"
        }
        script {
            name = "Wait for generation to finish"
            id = "RUNNER_74"
            scriptContent = "kubectl wait --for=condition=complete --timeout=1200s --namespace=hommits-production job/hommits-generate"
        }
        script {
            name = "Apply kubernetes config"
            id = "RUNNER_56"
            scriptContent = "kubectl apply -f production.yaml"
        }
        step {
            name = "Wait for kubernetes configuration to be applied"
            id = "RUNNER_57"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", "production.yaml")
            param("python-script-file-name", "wait_for_deployment.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        script {
            name = "Apply dashboard config"
            id = "RUNNER_81"
            scriptContent = "kubectl apply -f production.dashboard.yaml"
        }
        step {
            name = "Wait for dashboard configuration to be applied"
            id = "RUNNER_83"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", "production.dashboard.yaml")
            param("python-script-file-name", "wait_for_deployment.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
    }

    failureConditions {
        executionTimeoutMin = 30
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_13"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
        feature {
            id = "BUILD_EXT_17"
            type = "JetBrains.SharedResources"
            param("locks-param", "Deploy writeLock")
        }
    }

    dependencies {
        artifacts(HommitsCiScripts.buildTypes.HommitsCiScripts_Build) {
            id = "ARTIFACT_DEPENDENCY_6"
            buildRule = lastSuccessful()
            artifactRules = """
                python/extract-git-info.py
                python/generate-tag.py
                python/template-config.py
                python/kubernetes/production.yaml => templates/
                python/kubernetes/production.dashboard.yaml => templates/
                python/kubernetes/production.migrations.yaml => templates/
                python/kubernetes/production.generation.yaml => templates/
                python/wait_for_deployment.py
                python/extract-tenants.py
            """.trimIndent()
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_10")
    }
})
