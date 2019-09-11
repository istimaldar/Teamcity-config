package Hommits.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script

object Hommits_CheckLocalization : Template({
    uuid = "62382132-7333-40e8-a4ed-02953623d881"
    name = "Check localization"

    buildNumberPattern = "%build.vcs.number%"

    params {
        param("localization.redundant_file", "Warning")
        param("localization.empty_key", "Warning")
        param("localization.no_file", "Warning")
        param("localization.no_key", "Warning")
        param("localization.nedd_refactoring", "Warning")
        param("localization.required_languages", "ru")
        param("localization.same_key", "Warning")
        param("localization.redundant_key", "Warning")
    }

    steps {
        script {
            name = "Check localization"
            id = "RUNNER_39"
            workingDir = """src\Site\LocalizationValidation"""
            scriptContent = """.\check-localization.sh %netcore.version% --required-languages=%localization.required_languages% --no-key=%localization.no_key% --same-key=%localization.same_key% --empty-key=%localization.empty_key% --no-resource-file=%localization.no_file% --need-refactoring=%localization.nedd_refactoring% --redundant-key=%localization.redundant_key% --redundant-resource-file=%localization.redundant_file%"""
        }
    }

    failureConditions {
        executionTimeoutMin = 10
    }

    features {
        commitStatusPublisher {
            id = "BUILD_EXT_5"
            publisher = gitlab {
                gitlabApiUrl = "https://gitlab.com/api/v4"
                accessToken = "credentialsJSON:9b80a9e1-faa1-4e53-99d8-f5d0e2a69ee0"
            }
        }
    }

    requirements {
        doesNotEqual("machineType", "local", "RQ_9")
    }
})
