#!/usr/bin/env groovy

def call(def pkg, String cmd, Boolean dryrun){
    dir(pkg.repoPathGit) {
        if ( dryrun ) {
            echo "${cmd}"
        } else {
            sh "${cmd}"
        }
    }
}
