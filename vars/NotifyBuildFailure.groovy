#!/usr/bin/env groovy

def call(def pkg) {
    pkg.sendBuildMail('Failure', pkg.config.notify.fails, BUILD_URL)

    archiveArtifacts(
        allowEmptyArchive: true,
        artifacts: "${pkg.config.arch}**/*.log"
    )
}
