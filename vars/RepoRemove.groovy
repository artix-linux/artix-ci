#!/usr/bin/env groovy

def call(def pkg) {
    pkg.config.tools.cmdRepoRemove += " -d ${pkg.config.src.repoRemoveName}"
    catchError(message: "Errors occurred.", buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
        String args = pkg.info.packages.collect { it.pkgname }.join(' ')
        sh "${pkg.config.tools.cmdRepoRemove} ${args}"
    }
}
