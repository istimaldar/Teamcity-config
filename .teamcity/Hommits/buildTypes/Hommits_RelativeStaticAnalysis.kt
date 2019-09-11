package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_RelativeStaticAnalysis : Template({
    uuid = "6e16b945-6f19-4b90-81a0-4a7538295263"
    name = "Relative static analysis"

    buildNumberPattern = "%build.vcs.number%"

    params {
        param("tests.frontend.path", "frontend.xml")
        param("tests.backend.path", "tests.backend.xml")
    }

    steps {
        script {
            name = "CRUNCH! Generate config file."
            id = "RUNNER_53"
            workingDir = "src/Web"
            scriptContent = "echo '{}' > config.local.json"
        }
        step {
            name = "Download backend test results"
            id = "RUNNER_46"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --from=coverage.opencover.xml
                --to=%tests.backend.path%
                --id=%tests.backend.id%
                --number=%build.vcs.number%
                --uri=https://tc.centaurea.io/
                --login=%system.teamcity.auth.userId%
                --password=%system.teamcity.auth.password%
            """.trimIndent())
            param("python-script-file-name", "download-optional-dependency.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        step {
            name = "Download frontend test results"
            id = "RUNNER_47"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --from=test-reporter.xml
                --to=%tests.frontend.path%
                --id=%tests.frontend.id%
                --number=%build.vcs.number%
                --uri=https://tc.centaurea.io/
                --login=%system.teamcity.auth.userId%
                --password=%system.teamcity.auth.password%
            """.trimIndent())
            param("python-script-file-name", "download-optional-dependency.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        script {
            name = "Generate branch name"
            id = "RUNNER_58"
            scriptContent = """
                BRANCH=${'$'}(echo "%teamcity.build.branch%" | sed -e "s+/+_+g")
                echo "##teamcity[setParameter name='env.BRANCH_NAME' value='${'$'}{BRANCH}']"
            """.trimIndent()
        }
        step {
            name = "Create quality gate"
            id = "RUNNER_89"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --base-project=%sonar.project%_master
                --analysed-project=%sonar.project%_%env.BRANCH_NAME%
                --server-url=sonar.centaurea.io
                --token=%sonar.login%
                --measures=duplicated_blocks=0,duplicated_lines=2,duplicated_files=0,bugs=0,code_smells=0,violations=0
                --https
            """.trimIndent())
            param("python-script-file-name", "create-quality-gates.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
        script {
            name = "Start sonarqube analysis"
            id = "RUNNER_42"
            scriptContent = """dotnet sonarscanner begin /k:"%sonar.project%_%env.BRANCH_NAME%" /d:"sonar.host.url=%sonar.host.url%" /v:"%build.number%" /d:sonar.login=%sonar.login% /d:sonar.cs.opencover.reportsPaths="%tests.backend.path%" /d:sonar.coverage.exclusions="**Tests*.cs" /d:"sonar.exclusions=src/Web/wwwroot/__tests__/**/*" /d:sonar.testExecutionReportPaths="%tests.frontend.path%" /d:sonar.tests="src/Web/wwwroot/__tests__/""""
        }
        dotnetBuild {
            name = "Build project"
            id = "RUNNER_48"
            configuration = "Release"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        script {
            name = "Finish sonarqube analysis"
            id = "RUNNER_49"
            scriptContent = "dotnet sonarscanner end /d:sonar.login=%sonar.login%"
        }
        step {
            name = "Check quality qate result"
            id = "RUNNER_36"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --url=%sonar.host.url%
                --login=%sonar.login%
                --project=%sonar.project%_%env.BRANCH_NAME%
                --result-url=%sonar.external_address%
            """.trimIndent())
            param("python-script-file-name", "check-quality-gates.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
    }

    failureConditions {
        executionTimeoutMin = 40
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_8"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    dependencies {
        artifacts(HommitsCiScripts.buildTypes.HommitsCiScripts_Build) {
            id = "ARTIFACT_DEPENDENCY_7"
            buildRule = lastSuccessful()
            artifactRules = """
                python/download-optional-dependency.py => .
                python/check-quality-gates.py => .
                python/create-quality-gates.py => .
            """.trimIndent()
        }
    }

    requirements {
        equals("machineType", "local", "RQ_2")
    }
})
