#!/usr/bin/env groovy

def call(def pkg) {
    sh "${pkg.artixTools.signCmd} ${pkg.pkgInfo.pkgfile.join(' ')}"

    pkg.pkgActions.isAdd = true

    archiveArtifacts(allowEmptyArchive: true, artifacts: 'repos/**/*.log')
}
