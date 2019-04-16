#!/usr/bin/env groovy

def call(def pkg) {
    pkg.isBuildSuccess = true
    archiveArtifacts(allowEmptyArchive: true, artifacts: 'repos/**/*.log')
}
