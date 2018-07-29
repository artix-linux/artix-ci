#!/usr/bin/env groovy

def call(String repo, String cmd){
    dir(repo) {
        sh "${cmd}"
    }
}
