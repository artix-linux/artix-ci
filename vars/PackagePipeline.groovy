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
                        List<String> changeSet = helper.getChangeSet()
                        pkg.configurePkgRepo(changeSet)
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
                        script {
                            pkg.postBuild()
                        }
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
