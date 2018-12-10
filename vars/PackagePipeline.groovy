#!/usr/bin/env groovy

library 'loadChangeSet'
// library 'CheckNodes'
library 'BuildPkg'
library 'Notify'
library 'PostBuild'

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
                        checkout scm
                        loadChangeSet(pkg)
//                         CheckNodes(pkg)
                        echo "repoList: ${pkg.repoList}"
//                         echo "agentLabel: ${pkg.agentLabel}"
                        echo "pkgRepo: ${pkg.pkgRepo}"
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
//                         checkout scm
//                     }
                    BuildPkg(pkg.pkgRepo, pkg.buildArgs.join(' '), DRYRUN.toBoolean())
                }
                post {
                    success {
                        PostBuild(pkg)
                    }
                    failure {
                        Notify(pkg.pkgRepo)
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
                    DeployPkg(pkg.pkgRepo, pkg.addArgs.join(' '), DRYRUN.toBoolean())
                }
            }
            stage('Remove') {
//                 agent { label 'master' }
                when {
                    expression { return pkg.isRemove }
                }
                steps {
                    DeployPkg(pkg.pkgRepo, pkg.rmArgs.join(' '), DRYRUN.toBoolean())
                }
            }
        }
    }
}
