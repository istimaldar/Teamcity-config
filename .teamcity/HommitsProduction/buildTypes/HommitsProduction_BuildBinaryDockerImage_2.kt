package HommitsProduction.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object HommitsProduction_BuildBinaryDockerImage_2 : BuildType({
    templates(Hommits.buildTypes.Hommits_BuildBinaryDockerImage)
    uuid = "c70b0728-a9ee-4fd3-81fe-870a21ac190b"
    name = "Build binary docker image"

    vcs {
        root(HommitsProduction.vcsRoots.HommitsProductionVcsRoot)
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            triggerRules = "+:**"
        }
    }

    dependencies {
        dependency(HommitsProduction_PublishProject) {
            snapshot {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }

            artifacts {
                id = "ARTIFACT_DEPENDENCY_8"
                artifactRules = """
                    release.zip!%location.dlls% => %location.dlls%
                    release.zip!%location.nuget% => %location.nuget%
                    release.zip!%location.resources% => %location.resources%
                    release.zip!%location.static% => %location.static%
                """.trimIndent()
            }
        }
    }
})
