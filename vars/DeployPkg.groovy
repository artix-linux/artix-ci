#!/usr/bin/env groovy

def call(String repo, String cmd, Boolean debug){
    if ( fileExists(repo + '/PKGBUILD') ) {
        dir(repo) {
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
