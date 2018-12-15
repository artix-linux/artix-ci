#!/usr/bin/env groovy

def call(def pkg, Boolean debug) {
    String subject = ''
    String body = ''
    String msg = ''

    if ( pkg.isAdd ) {
        msg = 'repo-add'
        subject = "[Jenkins] ${msg}: ${JOB_NAME}"
        body = "<p><strong>${msg}</strong></p><p>Repo: ${pkg.repoAdd}</p><p>Packages: ${pkg.pkgName}</p><p><a href=${BUILD_URL}>${BUILD_URL}</a></p>"

        if ( pkg.isBuildSuccess ) {

            if ( ! debug ) {
                emailext (
                    body: body,
                    subject: subject,
                    to: "devs@artixlinux.org",
                    attachLog: true
                )
            }
        }

    } else if ( pkg.isRemove ) {
        msg = 'repo-remove'
        subject = "[Jenkins] ${msg}: ${JOB_NAME}"
        body = "<p><strong>${msg}</strong></p><p>Repo: ${pkg.repoRemove}</p><p>Packages: ${pkg.pkgName}</p><p><a href=${BUILD_URL}>${BUILD_URL}</a></p>"

        if ( ! debug ) {
            emailext (
                body: body,
                subject: subject,
                to: "devs@artixlinux.org",
                attachLog: true
            )
        }

    } else {
        msg = 'Failure'
        subject = "[Jenkins] ${msg}: ${JOB_NAME}"
        body = "<p><strong>${msg}</strong></p><p>Job: ${JOB_NAME}</p><p>Build: ${pkg.pkgRepo}</p><p><a href=${BUILD_URL}>${BUILD_URL}</a></p>"

        if ( ! debug ) {
            emailext (
                body: body,
                subject: subject,
                to: "devs@artixlinux.org",
                attachLog: true
            )
        }
    }
}
