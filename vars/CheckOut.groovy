#!/usr/bin/env groovy

def call(def pkg){
    checkout(scm)
    pkg.initialize()
    currentBuild.displayName = pkg.jobInfo.name
    currentBuild.description = pkg.jobInfo.desc
}
