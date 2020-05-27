#!/usr/bin/env groovy

def call(def pkg){
    pkg.artixConfig.tools.buildCmd += " -d ${pkg.artixConfig.tools.repoAddName}"
    Boolean isLibBump = false
    if ( pkg.artixConfig.tools.repoName == 'goblins' ) {
        isLibBump = input message: "Is this the first of a major staging rebuild?",
        parameters: [booleanParam(name: 'isLibBump', defaultValue: false, description: 'Select')]
    }
    echo "isLibBump: " + isLibBump
    if ( isLibBump ) {
        pkg.artixConfig.tools.buildCmd += " -m"
    }
    dir(pkg.artixConfig.tools.repoPathGit) {
        catchError(message: "FAILURE", buildResult: 'FAILURE', stageResult: 'FAILURE') {
            sh "${pkg.artixConfig.tools.buildCmd}"
        }
    }
}
