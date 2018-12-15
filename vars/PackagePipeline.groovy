#!/usr/bin/env groovy

// library 'loadChangeSet'
// library 'CheckNodes'
// library 'BuildPkg'
// library 'Notify'
// library 'PostBuild'
// library 'DeployPkg'

def call(def pkg) {
    pipeline {
//         agent none
        agent any
        options {
            skipDefaultCheckout()
            timestamps()
        }
        environment {
            DRYRUN = false
        }
        stages {
            stage('Prepare') {
//                 agent { label pkg.agentLabel }
                steps {
                    script {
                        checkout(scm)
                        loadChangeSet(pkg)
                        echo "repoList: ${pkg.repoList}"
                        echo "pkgRepo: ${pkg.pkgRepo}"
//                         CheckNodes(pkg)
//                         echo "agentLabel: ${pkg.agentLabel}"
                    }
                }
            }
            stage('Build') {
//                 agent { label pkg.agentLabel }
                when {
                    expression { return pkg.isBuild }
                }
                steps {
//                     script {
//                         checkout(scm)
//                     }
                    BuildPkg(pkg, DRYRUN.toBoolean())
                }
                post {
                    success {
                        PostBuild(pkg)
                    }
                    failure {
                        Notify(pkg, false, 'Failure', DRYRUN.toBoolean())
                    }
                }
            }
            stage('Add') {
//                 agent { label 'master' }
                environment {
                    BUILDBOT_GPGP = credentials('BUILDBOT_GPGP')
                }
                when {
                    anyOf {
                        expression { return pkg.isAdd }
                        expression { return pkg.isBuildSuccess }
                    }
                }
                steps {
                    DeployPkg(pkg, pkg.addArgs.join(' '), DRYRUN.toBoolean())
                }
                post {
                    always {
                        Notify(pkg, pkg.isAdd, 'repo-add', DRYRUN.toBoolean())
                    }
                }
            }
            stage('Remove') {
//                 agent { label 'master' }
                when {
                    expression { return pkg.isRemove }
                }
                steps {
                    DeployPkg(pkg, pkg.rmArgs.join(' '), DRYRUN.toBoolean())
                }
                post {
                    always {
                        Notify(pkg, pkg.isRemove, 'repo-remove', DRYRUN.toBoolean())
                    }
                }
            }
        }
    }
}
