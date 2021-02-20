#!/usr/bin/env groovy

def call(def pkg) {
    String msg = 'repo-add'

    pkg.sendRepoMail(msg)
}
