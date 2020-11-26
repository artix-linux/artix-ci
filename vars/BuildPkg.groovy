#!/usr/bin/env groovy

def call(def pkg){
    dir(pkg.config.src.repoPath) {
        catchError(
                message: "Failed to build source",
                buildResult: 'FAILURE',
                stageResult: 'FAILURE'
        ) {
            sh "${pkg.config.tools.cmdBuild}"
        }
    }
}
