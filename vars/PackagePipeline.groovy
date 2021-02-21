#!/usr/bin/env groovy

def call(def pkg) {
    pipeline {
        agent any
        options {
            skipDefaultCheckout()
            timestamps()
        }
        stages {
            stage('Prepare') {
                steps {
                    CheckOut(pkg)
                }
            }
            stage('Build') {
                environment {
                    BUILDBOT_GPGP = credentials('BUILDBOT_GPGP')
//                     BUILDBOT_GPGP = credentials("${pkg.author.gpgkey}")
                }
                when {
                    expression { return pkg.config.actions.isBuild }
                }
                steps {
                    BuildPkg(pkg)
                }
                post {
                    success { NotifyBuildSuccess(pkg) }
                    failure { NotifyBuildFailure(pkg) }
                }
            }
            stage('Add') {
                when {
                    expression { return pkg.config.actions.isAdd }
                }
                steps {
                    RepoAdd(pkg)
                }
                post {
                    always { pkg.sendRepoMail('repo-add') }
                }
            }
            stage('Remove') {
                when {
                    expression { return pkg.config.actions.isRemove }
                }
                steps {
                    RepoRemove(pkg)
                }
                post {
                    always { pkg.sendRepoMail('repo-remove') }
                }
            }
        }
    }
}
