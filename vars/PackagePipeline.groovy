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
                    checkout scm
                    script {
                        pkg.initialize()
                        currentBuild.displayName = pkg.jobInfo.name
                        currentBuild.description = pkg.jobInfo.desc
                    }
                }
            }
            stage('Build') {
                when {
                    expression { return pkg.isBuild }
                }
                steps {
                    BuildPkg(pkg, params.isDryRun)
                }
                post {
                    success {
                        PostBuild(pkg)
                    }
                    failure {
                        Notify(pkg, false, 'Failure', params.isDryRun)
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
                    DeployPkg(pkg, pkg.repoAddCmd, params.isDryRun)
                }
                post {
                    always {
                        Notify(pkg, pkg.isAdd, 'repo-add', params.isDryRun)
                    }
                }
            }
            stage('Remove') {
                when {
                    expression { return pkg.isRemove }
                }
                steps {
                    DeployPkg(pkg, pkg.repoRmCmd, params.isDryRun)
                }
                post {
                    always {
                        Notify(pkg, pkg.isRemove, 'repo-remove', params.isDryRun)
                    }
                }
            }
        }
    }
}
