#!/usr/bin/env groovy

def call(def pkg, String cmd, Boolean debug){
    dir(pkg.repoPathGit) {
        if ( debug ) {
            echo "${cmd}"
        } else {
            sh "${cmd}"
        }
    }
}
