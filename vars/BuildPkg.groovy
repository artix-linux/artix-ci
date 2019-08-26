#!/usr/bin/env groovy

def call(def pkg){
    if ( pkg.jobInfo.name == 'goblins' ) {
        pkg.isRebuild = input message: "Is this the first of a major staging rebuild?",
        parameters: [booleanParam(name: 'isRebuild', defaultValue: false, description: 'Select')]
    }
    echo "isRebuild: ${pkg.isRebuild}"
    dir(pkg.repoPathGit) {
        if ( params.isDryRun ) {
            echo "${pkg.buildCmd}"
        } else {
            sh "${pkg.buildCmd}"
        }
    }
}
