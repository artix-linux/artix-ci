@Library(['helper', 'BuildPkg', 'DeployPkg', 'RepoPackage']) import org.artixlinux.RepoPackage

def pkg = new RepoPackage(this)

def call() {
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
                    script {
                        BuildPkg(pkg.pkgRepo, pkg.buildArgs.join(' '))
                    }
                }
                post {
                    success {
                        script {
                            pkg.isBuildSuccess = true
                            pkg.addArgs.add('-s')
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
                    script {
                        DeployPkg(pkg.pkgTrunk, pkg.pkgRepo, pkg.addArgs.join(' '))
                    }
                }
            }
            stage('Remove') {
                when {
                    expression { return pkg.isRemove }
                }
                steps {
                    script {
                        DeployPkg(pkg.pkgTrunk, pkg.pkgRepo, pkg.rmArgs.join(' '))
                    }
                }
            }
        }
    }
}
