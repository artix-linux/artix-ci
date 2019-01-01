#!/usr/bin/env groovy

def call(def pkg, String cmd, Boolean debug){
    if ( fileExists(pkg.repoPathGit + '/PKGBUILD') ) {
        dir(pkg.repoPathGit) {
            if ( debug ) {
                echo "${cmd}"
            } else {
                sh "${cmd}"
            }
        }
    } else {
        dir('trunk') {
            if ( debug ) {
                echo "${cmd}"
            } else {
                sh "${cmd}"
            }
        }
    }
}
