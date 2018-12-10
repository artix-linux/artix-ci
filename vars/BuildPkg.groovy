#!/usr/bin/env groovy

def call(String repo, String cmd, Boolean debug){
    dir(repo) {
        if ( debug ) {
            echo "${cmd}"
        } else {
            sh "${cmd}"
        }
    }
}
