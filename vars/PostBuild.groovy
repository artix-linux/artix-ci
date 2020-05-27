#!/usr/bin/env groovy

def call(def pkg) {
    sh "${pkg.artixConfig.tools.signCmd} ${pkg.pkgInfo.pkgfile.join(' ')}"

    pkg.artixConfig.actions.isAdd = true

    archiveArtifacts(allowEmptyArchive: true, artifacts: 'repos/**/*.log')
}
