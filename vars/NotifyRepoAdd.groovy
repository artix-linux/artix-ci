#!/usr/bin/env groovy

def call(def pkg) {
    pkg.sendRepoMail('repo-add')
}
