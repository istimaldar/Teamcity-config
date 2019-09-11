package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_StartTestServer : Template({
    uuid = "db7785c7-c1f7-4ecc-8265-8786f0607a15"
    name = "Start test server"

    artifactRules = "tests.yaml"
    buildNumberPattern = "%build.vcs.number%"

    steps {
        step {
            name = "Extract git info"
            id = "RUNNER_17"
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
            id = "RUNNER_19"
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
            id = "RUNNER_28"
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
        script {
            name = "Apply kubernetes config"
            id = "RUNNER_40"
            scriptContent = "kubectl apply -f tests.yaml"
        }
        step {
            name = "Wait for kubernetes configuration to be applied"
            id = "RUNNER_43"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                tests.yaml
                --delete-on-fail
            """.trimIndent())
            param("python-script-file-name", "wait_for_deployment.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
    }

    failureConditions {
        executionTimeoutMin = 10
    }

    dependencies {
        artifacts(HommitsCiScripts.buildTypes.HommitsCiScripts_Build) {
            id = "ARTIFACT_DEPENDENCY_1"
            buildRule = lastSuccessful()
            artifactRules = """
                python/extract-git-info.py
                python/generate-tag.py
                python/template-config.py
                python/kubernetes/tests.yaml => templates/
                python/wait_for_deployment.py
            """.trimIndent()
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_18")
    }
})
