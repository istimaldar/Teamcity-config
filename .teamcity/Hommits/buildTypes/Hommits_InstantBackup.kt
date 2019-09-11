package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_InstantBackup : Template({
    uuid = "064b06e1-6ce2-4d31-942e-7bad8a210594"
    name = "Instant backup"

    steps {
        step {
            name = "Template backup config"
            id = "RUNNER_94"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --template-location=templates/backup.yaml
                -v=bucket=%bucket.name%
                --output-location=backup.yaml
            """.trimIndent())
            param("python-script-file-name", "template-config.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        script {
            name = "Backup"
            id = "RUNNER_95"
            scriptContent = "kubectl apply -f backup.yaml"
        }
    }

    dependencies {
        artifacts(HommitsCiScripts.buildTypes.HommitsCiScripts_Build) {
            id = "ARTIFACT_DEPENDENCY_10"
            buildRule = lastSuccessful()
            artifactRules = """
                python/kubernetes/backup.yaml => templates/
                python/template-config.py
            """.trimIndent()
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_13")
    }
})
