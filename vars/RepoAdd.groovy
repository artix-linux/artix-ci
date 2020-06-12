#!/usr/bin/env groovy

def call(def pkg) {
    pkg.config.tools.cmdRepoAdd += " -d ${pkg.config.src.repoAddName}"
    catchError(message: "Errors occurred.", buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
        String args = pkg.info.files.join(' ')
        sh "${pkg.config.tools.cmdRepoAdd} ${args}"
    }
}
