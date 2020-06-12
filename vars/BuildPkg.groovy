#!/usr/bin/env groovy

def call(def pkg){
    pkg.config.tools.cmdBuild += " -d ${pkg.config.src.repoAddName}"
    Boolean isLibBump = false
    if ( pkg.config.src.repoName == 'goblins' ) {
        isLibBump = input message: "Is this the first of a major staging rebuild?",
        parameters: [booleanParam(name: 'isLibBump', defaultValue: false, description: 'Select')]
    }
    echo "isLibBump: " + isLibBump
    if ( isLibBump ) {
        pkg.config.tools.cmdBuild += " -m"
    }
    dir(pkg.config.src.repoPath) {
        catchError(message: "Failed to build source", buildResult: 'FAILURE', stageResult: 'FAILURE') {
            sh "${pkg.config.tools.cmdBuild}"
        }
    }
}
