#!/usr/bin/env groovy

def call(def pkg, String cmd, Boolean debug){

    if ( pkg.repoList.size() > 0 ) {
        String pkgYaml = sh(returnStdout: true, script: "pkg2yaml ${pkg.pkgRepo}")
        def pkgInfo = readYaml text: pkgYaml
        pkg.pkgName = pkgInfo.pkgname
        pkg.pkgFile = pkgInfo.pkgfile
    }
//     echo "pkgName: ${pkg.pkgName}"
//     echo "pkgFile: ${pkg.pkgFile}"

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
