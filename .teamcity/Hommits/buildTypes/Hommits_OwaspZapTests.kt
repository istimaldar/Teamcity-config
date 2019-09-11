package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_OwaspZapTests : Template({
    uuid = "7d1c0104-57a7-4551-a4da-2ebf66a337ff"
    name = "OWASP ZAP tests"

    artifactRules = "reports/*.html => ./"
    buildNumberPattern = "%build.vcs.number%"

    steps {
        script {
            name = "Start OWASP ZAP"
            id = "RUNNER_1"
            scriptContent = "zap-cli -v start --start-options '-config api.disablekey=true'"
        }
        script {
            name = "Run tests"
            id = "RUNNER_2"
            scriptContent = "zap-cli -v quick-scan -s xss,sqli --spider -r %staging.url% || :"
        }
        script {
            name = "Make directory for reports"
            id = "RUNNER_3"
            scriptContent = "mkdir -p reports"
        }
        script {
            name = "Genereate reports"
            id = "RUNNER_4"
            scriptContent = "zap-cli -v report -o reports/report.html -f html"
        }
        script {
            name = "Shutdown OWASP ZAP"
            id = "RUNNER_5"
            executionMode = BuildStep.ExecutionMode.ALWAYS
            scriptContent = "zap-cli -v shutdown"
        }
    }

    failureConditions {
        executionTimeoutMin = 200
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_15"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    requirements {
        equals("machineType", "local", "RQ_14")
    }
})
