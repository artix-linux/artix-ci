#!/usr/bin/env groovy

def call(def pkg) {
    sh "${pkg.config.tools.cmdSign}"
    pkg.config.actions.isAdd = true

    String msg = 'build'

    pkg.sendBuildMail(msg, pkg.config.notify.builds, BUILD_URL)

    archiveArtifacts(
        allowEmptyArchive: true,
        artifacts: "${pkg.config.arch}**/*.log"
    )
}
