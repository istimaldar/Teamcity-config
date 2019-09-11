package HommitsProduction.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

object HommitsProductionVcsRoot : GitVcsRoot({
    uuid = "5482f710-371b-419d-a923-f5c2ea4c0c84"
    name = "Hommits production"
    url = "https://gitlab.com/centaurea.io/hommits.git"
    branch = "refs/heads/830-improve-error-reports"
    branchSpec = "+:refs/heads/(830-improve-error-reports)"
    authMethod = password {
        userName = "istimaldar"
        password = "credentialsJSON:3425bd29-556b-4b6c-ab6c-a635347450f7"
    }
})
