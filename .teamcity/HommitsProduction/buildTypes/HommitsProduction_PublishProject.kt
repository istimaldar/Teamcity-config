package HommitsProduction.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs

object HommitsProduction_PublishProject : BuildType({
    templates(Hommits.buildTypes.Hommits_PublishProject)
    uuid = "3a36180e-9686-401e-b658-615fc98dcf12"
    name = "Publish project"

    vcs {
        root(HommitsProduction.vcsRoots.HommitsProductionVcsRoot)
    }

    triggers {
        vcs {
            id = "vcsTrigger"
            triggerRules = "+:**"
        }
    }
})
