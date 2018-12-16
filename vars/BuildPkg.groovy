#!/usr/bin/env groovy

def call(def pkg, Boolean debug){

    String cmd = pkg.buildCmd

    dir(pkg.pkgRepo) {
        if ( debug ) {
            echo "${cmd}"
        } else {
            sh "${cmd}"
        }
    }
}
