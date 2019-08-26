#!/usr/bin/env groovy

def call(def pkg) {
    pipeline {
        agent any
        options {
            skipDefaultCheckout()
            timestamps()
        }
        parameters {
            booleanParam(name: 'isDryRun', defaultValue: false, description: 'Disable build and deploy commands?')
        }
        stages {
            stage('Prepare') {
                steps {
                    CheckOut(pkg)
                }
            }
            stage('Build') {
                when {
                    expression { return pkg.isBuild }
                }
                steps {
                    BuildPkg(pkg)
                }
                post {
                    success {
                        PostBuild(pkg)
                    }
                    failure {
                        Notify(pkg, 'Failure')
                    }
                }
            }
            stage('Add') {
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
                    DeployPkg(pkg, pkg.repoAddCmd)
                }
                post {
                    always {
                        Notify(pkg, 'repo-add')
                    }
                }
            }
            stage('Remove') {
                when {
                    expression { return pkg.isRemove }
                }
                steps {
                    DeployPkg(pkg, pkg.repoRmCmd)
                }
                post {
                    always {
                        Notify(pkg, 'repo-remove')
                    }
                }
            }
        }
    }
}
