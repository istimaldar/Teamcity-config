package Hommits.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

object Hommits_HttpsGithubComIstimaldarTeamcityConfigGit : GitVcsRoot({
    uuid = "dfaa58ea-b3d3-4281-87d7-2a3b8f59796e"
    name = "https://github.com/istimaldar/Teamcity-config.git"
    url = "https://github.com/istimaldar/Teamcity-config.git"
    authMethod = password {
        userName = "istimaldar"
        password = "credentialsJSON:3425bd29-556b-4b6c-ab6c-a635347450f7"
    }
})
