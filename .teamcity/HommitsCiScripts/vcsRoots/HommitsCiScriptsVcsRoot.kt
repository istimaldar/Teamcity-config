package HommitsCiScripts.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

object HommitsCiScriptsVcsRoot : GitVcsRoot({
    uuid = "47f7e8a2-d92e-4726-84f2-0b6670a763ec"
    name = "https://gitlab.com/centaurea.io/hommits-ci-scripts#refs/heads/master"
    url = "https://gitlab.com/centaurea.io/hommits-ci-scripts.git"
    branchSpec = "+:refs/heads/(master)"
    authMethod = password {
        userName = "istimaldar"
        password = "credentialsJSON:3425bd29-556b-4b6c-ab6c-a635347450f7"
    }
})
