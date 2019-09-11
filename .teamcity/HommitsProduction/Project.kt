package HommitsProduction

import HommitsProduction.buildTypes.*
import HommitsProduction.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.Project

object Project : Project({
    uuid = "79f8b3c6-7282-4462-a56b-3026100fa29e"
    id("HommitsProduction")
    parentId("Hommits")
    name = "Production"

    vcsRoot(HommitsProductionVcsRoot)

    buildType(HommitsProduction_RunSmokeTestsForUsa)
    buildType(HommitsProduction_RunSmokeTestsForBelarus)
    buildType(HommitsProduction_RunSmokeTestsForNewzealand)
    buildType(HommitsProduction_RunSmokeTestsForUk)
    buildType(HommitsProduction_DeployOnProduction)
    buildType(HommitsProduction_BuildBinaryDockerImage_2)
    buildType(HommitsProduction_BuildMigrationDockerImage)
    buildType(HommitsProduction_BuildGenerationDockerImage)
    buildType(HommitsProduction_PublishProject)
    buildType(HommitsProduction_RunSmokeTestsForRussia)

    cleanup {
        artifacts(builds = 2)
    }
    buildTypesOrder = arrayListOf(HommitsProduction_BuildGenerationDockerImage, HommitsProduction_BuildMigrationDockerImage, HommitsProduction_PublishProject, HommitsProduction_BuildBinaryDockerImage_2, HommitsProduction_DeployOnProduction)
})
