package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*

object Hommits_CheckIfMergeRequestShouldBeBuilded : Template({
    uuid = "2fe05ecc-0a9f-4bed-be78-3fa03e3d6608"
    name = "Check if merge request should be builded"

    buildNumberPattern = "%build.vcs.number%"

    steps {
        step {
            name = "Check if branch should be build"
            id = "RUNNER_65"
            type = "python"
            param("python-kind", "C")
            param("python-arguments", """
                --gitlab-token=%gitlab.login%
                --project-id=%hommits.id%
                --request-id=%teamcity.build.branch%
            """.trimIndent())
            param("python-script-file-name", "apply-branch-filter.py")
            param("python-exe", "%Python.3%")
            param("python-ver", "3")
            param("python-script-mode", "file")
        }
    }

    dependencies {
        artifacts(HommitsCiScripts.buildTypes.HommitsCiScripts_Build) {
            id = "ARTIFACT_DEPENDENCY_2"
            buildRule = lastSuccessful()
            artifactRules = "python/apply-branch-filter.py"
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_8")
    }
})
