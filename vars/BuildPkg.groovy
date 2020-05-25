#!/usr/bin/env groovy

def call(def pkg){
    pkg.artixTools.buildCmd += " -d ${pkg.artixTools.repoAddName}"
    Boolean isLibBump = false
    if ( pkg.jobInfo.name == 'goblins' ) {
        isLibBump = input message: "Is this the first of a major staging rebuild?",
        parameters: [booleanParam(name: 'isLibBump', defaultValue: false, description: 'Select')]
    }
    echo "isLibBump: " + isLibBump
    if ( isLibBump ) {
        pkg.artixTools.buildCmd += " -m"
    }
    dir(pkg.artixTools.repoPathGit) {
        catchError(message: "FAILURE", buildResult: 'FAILURE', stageResult: 'FAILURE') {
            sh "${pkg.artixTools.buildCmd}"
        }
    }
}
