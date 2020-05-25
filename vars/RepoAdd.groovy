#!/usr/bin/env groovy

def call(def pkg) {
    pkg.artixTools.repoAddCmd += " -d ${pkg.artixTools.repoAddName}"
    catchError(message: "Errors occurred.", buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
        sh "${pkg.artixTools.repoAddCmd} ${pkg.pkgInfo.pkgfile.join(' ')}"
    }
}
