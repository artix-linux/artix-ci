#!/usr/bin/env groovy

def call(def pkg) {
    String msg = 'repo-remove'

    pkg.sendRepoMail(msg)
}
