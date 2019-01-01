#!/usr/bin/env groovy

def call(def pkg, Boolean debug){

    String cmd = pkg.buildCmd

    dir(pkg.repoPathGit) {
        if ( debug ) {
            echo "${cmd}"
        } else {
            sh "${cmd}"
        }
    }
}
