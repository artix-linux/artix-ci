#!/usr/bin/env groovy

def call(String trunk, String repo, String cmd){
    if ( fileExists(repo + '/PKGBUILD') ) {
        dir(repo) {
            echo cmd
        }
    } else {
        dir(trunk) {
            echo cmd
        }
    }
}
