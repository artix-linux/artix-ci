#!/usr/bin/env groovy

def call(def pkg) {
    sh "${pkg.config.tools.cmdSign}"
    pkg.config.actions.isAdd = true
    archiveArtifacts(
        allowEmptyArchive: true,
        artifacts: "${pkg.config.arch}**/*.log"
    )
}
