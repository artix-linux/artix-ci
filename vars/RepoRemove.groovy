#!/usr/bin/env groovy

def call(def pkg) {
    pkg.artixTools.repoRemoveCmd += " -d ${pkg.artixTools.repoRemoveName}"
    catchError(message: "Errors occurred.", buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
        sh "${pkg.artixTools.repoRemoveCmd} ${pkg.pkgInfo.pkgfile.join(' ')}"
    }
}
