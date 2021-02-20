#!/usr/bin/env groovy

def call(def pkg) {
    String msg = 'Failure'

    pkg.sendBuildMail(msg, pkg.config.notify.fails, BUILD_URL)

    archiveArtifacts(
        allowEmptyArchive: true,
        artifacts: "${pkg.config.arch}**/*.log"
    )
}
