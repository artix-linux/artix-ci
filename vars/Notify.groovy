#!/usr/bin/env groovy

def call(def pkg, Boolean repoops, String msg, Boolean dryrun) {
    String subject = ''
    String body = ''
    String bodyInfo = ''
    String bodyAction = ''
    String bodyRepo = "<p><strong>${msg}</strong></p>"
    String bodyAuthor = "<p>authorName: ${pkg.authorInfo.name}</p><p>authorEmail: ${pkg.authorInfo.email}</p>"
    String bodyUrl = "<p><a href=${BUILD_URL}>${BUILD_URL}</a></p>"

    Boolean sendMail = false
    Boolean isLogAttach = false
    String sendTo = 'artix-builds@artixlinux.org'

    if ( repoops ) {
        bodyInfo = "<p>Packages: ${pkg.pkgInfo.pkgfile}</p>"

        if ( msg == 'repo-add' ) {
            sendMail = true
            subject = "[${pkg.repoAdd}] ${msg}: ${pkg.pkgInfo.pkgbase.pkgname}"
            bodyAction = "<p>Repo: ${pkg.repoAdd}</p>"
        } else if ( msg == 'repo-remove' ) {
            if ( ! pkg.isBuildSuccess ) {
                sendMail = true
                subject = "[${pkg.repoRemove}] ${msg}: ${pkg.pkgInfo.pkgbase.pkgname}"
                bodyAction = "<p>Repo: ${pkg.repoRemove}</p>"
            }
        }
    } else {
        subject = "[${pkg.repoAdd}] ${msg}: ${pkg.pkgInfo.pkgbase.pkgname}"
        bodyInfo = "<p>Job: ${JOB_NAME}</p><p>Packages: ${pkg.pkgInfo.pkgfile}</p>"
        bodyAction = "<p>Build: ${pkg.repoPathGit}</p>"

        sendMail = true

        if ( msg == 'Failure' ) {
            sendTo = 'artix-build-failures@artixlinux.org'
            isLogAttach = true
        }
    }

    body = "${bodyRepo}${bodyAction}${bodyInfo}${bodyAuthor}${bodyUrl}"

    if ( sendMail ) {
        if ( ! dryrun ) {
            emailext (
                body: body,
                subject: subject,
                to: sendTo,
                attachLog: isLogAttach
            )
        }
    }
}
