package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_DeployOnTestServer : Template({
    uuid = "2c761b2f-5b95-46c2-94ae-4a66a0392f11"
    name = "Deploy on test server"

    enablePersonalBuilds = false
    type = BuildTypeSettings.Type.DEPLOYMENT
    buildNumberPattern = "%build.vcs.number%"
    maxRunningBuilds = 1

    steps {
        step {
            name = "Extract git info"
            id = "RUNNER_93"
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
            id = "RUNNER_96"
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
            id = "RUNNER_99"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --template-location=templates/tests.yaml
                -v=tag=%tag.name%
                -v=registry=%registry.external_address%
                --output-location=tests.yaml
            """.trimIndent())
            param("python-script-file-name", "template-config.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        step {
            name = "Template generation config"
            id = "RUNNER_100"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --template-location=templates/tests.generation.yaml
                -v=tag=%tag.name%
                -v=registry=%registry.external_address%
                --output-location=tests.generation.yaml
            """.trimIndent())
            param("python-script-file-name", "template-config.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        script {
            name = "Remove all existing jobs"
            id = "RUNNER_101"
            scriptContent = "kubectl delete jobs --namespace=infrastructure ${'$'}(kubectl get jobs --namespace=infrastructure -o jsonpath='{.items[*].metadata.name}') || :"
        }
        script {
            name = "Apply kubernetes config"
            id = "RUNNER_102"
            scriptContent = "kubectl apply -f tests.yaml"
        }
        step {
            name = "Wait for kubernetes configuration to be applied"
            id = "RUNNER_103"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", "tests.yaml")
            param("python-script-file-name", "wait_for_deployment.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        script {
            name = "Apply generation"
            id = "RUNNER_104"
            scriptContent = "kubectl apply -f tests.generation.yaml"
        }
        script {
            name = "Wait for generation to finish"
            id = "RUNNER_105"
            scriptContent = "kubectl wait --for=condition=complete --timeout=1200s --namespace=infrastructure job/tests-generate"
        }
    }

    features {
        feature {
            id = "BUILD_EXT_21"
            type = "JetBrains.SharedResources"
            param("locks-param", "Deploy writeLock")
        }
        commitStatusPublisher {
            id = "BUILD_EXT_22"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    dependencies {
        artifacts(HommitsCiScripts.buildTypes.HommitsCiScripts_Build) {
            id = "ARTIFACT_DEPENDENCY_13"
            buildRule = lastSuccessful()
            artifactRules = """
                python/extract-git-info.py
                python/generate-tag.py
                python/template-config.py
                python/kubernetes/tests.yaml => templates/
                python/kubernetes/tests.generation.yaml => templates/
                python/wait_for_deployment.py
            """.trimIndent()
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_12")
    }
})
