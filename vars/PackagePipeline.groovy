#!/usr/bin/env groovy

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
                    checkout scm
                    script {
                        pkg.initialize()
                        echo "pkgInfo: ${pkg.pkgInfo}"
                    }
                }
            }
            stage('Build') {
//                 agent { label pkg.agentLabel }
                when {
                    expression { return pkg.isBuild }
                }
                steps {
//                     checkout scm
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
                    DeployPkg(pkg, pkg.repoAddCmd, DRYRUN.toBoolean())
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
                    DeployPkg(pkg, pkg.repoRmCmd, DRYRUN.toBoolean())
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
