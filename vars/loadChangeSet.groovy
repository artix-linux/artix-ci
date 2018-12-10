#!/usr/bin/env groovy

def call(def pkg) {
    String gitCmd = 'git rev-parse @'
    String commit = sh(returnStdout: true, script: gitCmd).trim()

    gitCmd = "git show --pretty=format: --name-status ${commit}"
    List<String> changeSet = sh(returnStdout: true, script: gitCmd).tokenize('\n')

    for ( int i = 0; i < changeSet.size(); i++ ) {
        List<String> entry = changeSet[i].split()
        String fileStatus = entry[0]
        for ( int j = 1; j < entry.size(); j++ ) {
            if ( entry[j].contains('/PKGBUILD') && entry[j].contains('repos/') ){
                Map dataSet = [status: fileStatus, path: entry[j].minus('/PKGBUILD')]
                pkg.repoList.add(dataSet)
            }
        }
    }
    pkg.initialize()
}
