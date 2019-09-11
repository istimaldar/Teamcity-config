package Hommits

import Hommits.buildTypes.*
import Hommits.vcsRoots.*
import Hommits.vcsRoots.Hommits_HttpsGithubComIstimaldarTeamcityConfigGit
import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.Project
import jetbrains.buildServer.configs.kotlin.v2018_2.projectFeatures.VersionedSettings
import jetbrains.buildServer.configs.kotlin.v2018_2.projectFeatures.versionedSettings

object Project : Project({
    uuid = "275a0d70-700b-43f4-b941-16fc4997dc1a"
    id("Hommits")
    parentId("_Root")
    name = "Hommits"

    vcsRoot(Hommits_HttpsGithubComIstimaldarTeamcityConfigGit)

    template(Hommits_TestFrontend)
    template(Hommits_BuildFrontend)
    template(Hommits_CheckIfMergeRequestShouldBeBuilded)
    template(Hommits_CheckLocalization)
    template(Hommits_DeployOnTestServer)
    template(Hommits_BuildBackend)
    template(Hommits_DeployOnStaging)
    template(Hommits_BuildMigrationsDockerImage)
    template(Hommits_DeployOnProduction)
    template(Hommits_StopTestServer)
    template(Hommits_StaticAnalysis)
    template(Hommits_BuildBinaryDockerImage)
    template(Hommits_TestBackend)
    template(Hommits_BuildGenerationDockerImage)
    template(Hommits_OwaspZapTests)
    template(Hommits_PublishProject)
    template(Hommits_RunE2eTests)
    template(Hommits_StartTestServer)
    template(Hommits_RunSmokeTests)
    template(Hommits_RelativeStaticAnalysis)
    template(Hommits_InstantBackup)

    params {
        param("development", "yes")
        param("projects", "Hommits.sln")
        param("netcore.version", "netcoreapp2.2")
        param("hommits.id", "9846822")
        param("sonar.host.url", "https://sonar.centaurea.io")
        param("sonar.login", "c3b41ee98c50ee5c9b92a9df4073ecff6c13a051")
        param("sonar.external_address", "sonar.istimaldar.xyz")
        param("location.resources", "resources/")
        param("artefact.path", "out")
        param("location.owasp_reports", "reports")
        param("staging.url", "https://hm.stg.centaurea.io/")
        param("gitlab.login", "euBMqsS9b4ZHEzscazX7")
        param("solution.name", "Hommits")
        param("deploy.folder", "deploy_hommits")
        param("coverage.path", "%artefact.path%/coverage/")
        param("publish.path", "published/")
        param("sonar.project", "Hommits")
        param("registry.external_address", "docker.istimaldar.xyz")
        param("location.static", "static/")
        param("location.dlls", "dlls/")
        param("bucket.name", "hm-db-bkp")
        param("location.nuget", "nuget/")
    }

    features {
        feature {
            id = "PROJECT_EXT_3"
            type = "OAuthProvider"
            param("clientId", "65a3bb3d760a7893ff25f8b46e2ee14229a53ce325bd7bf9cf4bf52f19372acc")
            param("secure:clientSecret", "credentialsJSON:e46e43b1-03a3-448c-8d9a-3b83399ae646")
            param("displayName", "GitLab.com")
            param("providerType", "GitLabCom")
        }
        feature {
            id = "PROJECT_EXT_4"
            type = "sonar-qube"
            param("password", "zxxf8e470fafdd7b63e1dec2c1b12f1f3f1")
            param("jdbcUrl", "jdbc:postgresql://database/sonar")
            param("name", "K8S sonar")
            param("jdbcPassword", "zxx323c53f7fbc967a7")
            param("id", "11270f98-8f60-447f-95d5-9caef05800ef")
            param("login", "teamcity")
            param("jdbcUsername", "sonar")
            param("url", "http://sonar")
        }
        feature {
            id = "PROJECT_EXT_5"
            type = "JetBrains.SharedResources"
            param("quota", "1")
            param("name", "TestServer")
            param("type", "quoted")
        }
        feature {
            id = "PROJECT_EXT_6"
            type = "JetBrains.SharedResources"
            param("quota", "1")
            param("name", "Deploy")
            param("type", "quoted")
        }
        versionedSettings {
            id = "PROJECT_EXT_7"
            mode = VersionedSettings.Mode.ENABLED
            buildSettingsMode = VersionedSettings.BuildSettingsMode.USE_CURRENT_SETTINGS
            rootExtId = "${Hommits_HttpsGithubComIstimaldarTeamcityConfigGit.id}"
            showChanges = false
            settingsFormat = VersionedSettings.Format.KOTLIN
            storeSecureParamsOutsideOfVcs = true
        }
    }

    cleanup {
        all(days = 7)
        history(days = 7)
        artifacts(days = 7)
    }
    subProjectsOrder = arrayListOf(AbsoluteId("HommitsCiScripts"), AbsoluteId("HommitsProduction"))
})
