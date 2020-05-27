#!/usr/bin/env groovy

def call(def pkg){
    checkout(scm)
    pkg.initialize()
    currentBuild.displayName = pkg.artixConfig.tools.repoName
    currentBuild.description = pkg.pkgInfo.pkgbase.pkgname + '-' + pkg.pkgInfo.pkgbase.fullver
}
