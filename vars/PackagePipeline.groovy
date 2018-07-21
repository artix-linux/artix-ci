#!/usr/bin/env groovy

def call(def pkg) {
    pipeline {
        agent any
        options {
            skipDefaultCheckout()
            timestamps()
        }
        stages {
            stage('Checkout') {
                steps {
                    script {
                        checkout scm
                        String commit = sh(returnStdout: true, script: 'git rev-parse @').trim()
                        List<String> changeSet = sh(returnStdout: true, script: "git show --pretty=format: --name-status ${commit}").tokenize('\n')
                        pkg.configureRepo(changeSet)
                    }
                }
            }
            stage('Build') {
                when {
                    expression { return pkg.isBuild }
                }
                steps {
                    BuildPkg(pkg.pkgRepo, pkg.buildArgs.join(' '))
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
                    DeployPkg(pkg.pkgTrunk, pkg.pkgRepo, pkg.addArgs.join(' '))
                }
            }
            stage('Remove') {
                when {
                    expression { return pkg.isRemove }
                }
                steps {
                    DeployPkg(pkg.pkgTrunk, pkg.pkgRepo, pkg.rmArgs.join(' '))
                }
            }
        }
    }
}
