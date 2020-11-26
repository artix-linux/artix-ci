#!/usr/bin/env groovy

def call(def pkg) {
    catchError(
        message: "Errors occurred.",
        buildResult: 'UNSTABLE',
        stageResult: 'UNSTABLE'
    ) {
        sh "${pkg.config.tools.cmdRepoRemove}"
    }
}
