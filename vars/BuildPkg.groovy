#!/usr/bin/env groovy

def call(def pkg, Boolean dryrun){

    String cmd = pkg.buildCmd

    dir(pkg.repoPathGit) {
        if ( dryrun ) {
            echo "${cmd}"
        } else {
            sh "${cmd}"
        }
    }
}
