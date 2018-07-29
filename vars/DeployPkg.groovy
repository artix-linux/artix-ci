#!/usr/bin/env groovy

def call(String trunk, String repo, String cmd){
    if ( fileExists(repo + '/PKGBUILD') ) {
        dir(repo) {
            sh "${cmd}"
        }
    } else {
        dir(trunk) {
            sh "${cmd}"
        }
    }
}
