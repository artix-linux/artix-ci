#!/usr/bin/env groovy

def call(def pkg) {
    pkg.artixConfig.tools.repoAddCmd += " -d ${pkg.artixConfig.tools.repoAddName}"
    catchError(message: "Errors occurred.", buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
        sh "${pkg.artixConfig.tools.repoAddCmd} ${pkg.pkgInfo.pkgfile.join(' ')}"
    }
}
