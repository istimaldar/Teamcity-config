package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_StopTestServer : Template({
    uuid = "1c144672-8a19-4ca5-bbd6-105a58e6c817"
    name = "Stop test server"

    buildNumberPattern = "%build.vcs.number%"

    steps {
        script {
            id = "RUNNER_44"
            scriptContent = "kubectl delete -f tests.yaml"
        }
    }

    failureConditions {
        executionTimeoutMin = 5
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_19")
    }
})
