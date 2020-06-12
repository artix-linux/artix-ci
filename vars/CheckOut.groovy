#!/usr/bin/env groovy

def call(def pkg){
    catchError(message: "Failed to prepare source", buildResult: 'FAILURE', stageResult: 'FAILURE') {
        checkout(scm)
        pkg.initialize()
        currentBuild.displayName = pkg.config.src.repoName
        currentBuild.description = pkg.info.pkgbase.name + '-' + pkg.info.version
    }
}
