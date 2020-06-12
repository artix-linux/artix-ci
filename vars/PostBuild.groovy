#!/usr/bin/env groovy

def call(def pkg) {
    sh "${pkg.config.tools.cmdSign} ${pkg.info.files.join(' ')}"

    pkg.config.actions.isAdd = true

    archiveArtifacts(allowEmptyArchive: true, artifacts: 'repos/**/*.log')
}
