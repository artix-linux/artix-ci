#!/usr/bin/env groovy

def call(def pkg, Boolean repoops, String msg, Boolean debug) {
    String subject = ''
    String body = ''
    Boolean sendMail = false
    if ( repoops ) {
        subject = "[Jenkins] ${msg}: ${JOB_NAME}"

        if ( msg == 'repo-add' ) {

            body = "<p><strong>${msg}</strong></p><p>Repo: ${pkg.repoAdd}</p><p>Packages: ${pkg.pkgName}</p><p><a href=${BUILD_URL}>${BUILD_URL}</a></p>"

            if ( pkg.isBuildSuccess ) {
                sendMail = true
            }

        } else if ( msg == 'repo-remove' ) {

            body = "<p><strong>${msg}</strong></p><p>Repo: ${pkg.repoRemove}</p><p>Packages: ${pkg.pkgName}</p><p><a href=${BUILD_URL}>${BUILD_URL}</a></p>"
            sendMail = true

        }

        if ( sendMail ) {

            if ( ! debug ) {
                emailext (
                    body: body,
                    subject: subject,
                    to: "devs@artixlinux.org",
                    attachLog: true
                )
            }

        }


    } else {
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
