#!/usr/bin/env groovy

def call(def pkg, String cmd, Boolean debug){

    if ( pkg.repoList.size() > 0 ) {
        String pkgYaml = sh(returnStdout: true, script: "pkg2yaml ${pkg.pkgRepo}")
        def pkgBuild = readYaml text: pkgYaml
        pkg.pkgName = pkgBuild.pkgname
    }
    echo "pkgName: ${pkg.pkgName}"

    if ( fileExists(pkg.pkgRepo + '/PKGBUILD') ) {
        dir(pkg.pkgRepo) {
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
