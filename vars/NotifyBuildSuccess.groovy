#!/usr/bin/env groovy

def call(def pkg) {
    sh "${pkg.config.tools.cmdSign}"
    pkg.config.actions.isAdd = true

    // maybe only send namcap & check logs?
//     pkg.sendBuildMail('Success', pkg.config.notify.builds, BUILD_URL)

    archiveArtifacts(
        allowEmptyArchive: true,
        artifacts: "${pkg.config.arch}**/*.log"
    )
}
