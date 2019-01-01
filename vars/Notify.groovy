#!/usr/bin/env groovy

def call(def pkg, Boolean repoops, String msg, Boolean debug) {
    String subject = "${msg}: ${pkg.pkgInfo.pkgbase.pkgname}"
    String body = ''
    String bodyInfo = ""
    String bodyAction = ''
    string bodyRepo = "<p><strong>${msg}</strong></p>"
    string bodyAuthor = "<p>authorName: ${pkg.authorInfo.name}</p><p>authorEmail: ${pkg.authorInfo.email}</p>"
    String bodyUrl = "<p><a href=${BUILD_URL}>${BUILD_URL}</a></p>"

    Boolean sendMail = false
    String sendTo = 'artix-builds@artixlinux.org'

    if ( repoops ) {
        if ( msg == 'repo-add' ) {
            bodyInfo = "<p>Packages: ${pkg.pkgInfo.pkgfile}</p>"
            bodyAction = "<p>Repo: ${pkg.repoAdd}</p>"
            sendMail = true
        } else if ( msg == 'repo-remove' ) {
            bodyInfo = "<p>Packages: ${pkg.pkgInfo.pkgfile}</p>"
            bodyAction = "<p>Repo: ${pkg.repoRemove}</p>"
            if ( ! pkg.isBuildSuccess ) {
                sendMail = true
            }
        }
    } else {
        bodyInfo = "<p>Job: ${JOB_NAME}</p>"
        bodyAction = "<p>Build: ${pkg.repoPathGit}</p>"

        sendMail = true

        if ( msg == 'Failure' ) {
            sendTo = 'artix-build-failures@artixlinux.org'
        }
    }

    body = "${bodyRepo}${bodyAction}${bodyInfo}${bodyAuthor}${bodyUrl}"

    if ( sendMail ) {
        if ( ! debug ) {
            emailext (
                body: body,
                subject: subject,
                to: sendTo,
                attachLog: true
            )
        }
    }
}
