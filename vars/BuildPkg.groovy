#!/usr/bin/env groovy

def call(def pkg){
    Boolean isLibBump = false
    if ( pkg.config.src.repoName == 'goblins' ) {
        isLibBump = input message: "Is this a *.so version bump to cause rebuilds?",
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
