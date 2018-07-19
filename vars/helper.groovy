#!/usr/bin/env groovy

List<String> getChangeSet() {
    String commit = sh(returnStdout: true, script: 'git rev-parse @').trim()
    return sh(returnStdout: true, script: "git show --pretty=format: --name-status ${commit}").tokenize('\n')
}
