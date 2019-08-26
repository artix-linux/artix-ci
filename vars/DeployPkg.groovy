#!/usr/bin/env groovy

def call(def pkg, String cmd){
    dir(pkg.repoPathGit) {
        String args = pkg.pkgInfo.pkgfile.join(' ')
        if ( params.isDryRun ) {
            echo "${cmd} ${args}"
        } else {
            sh "${cmd} ${args}"
        }
    }
}
