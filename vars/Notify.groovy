#!/usr/bin/env groovy

def call(def pkg, String msg) {
    Boolean sendMail = false
    Boolean isLogAttach = false

    String msgSubject = "${msg}: ${pkg.pkgInfo.pkgbase.pkgname}"
    String subject = ''
    String bodyAction = ''
    String bodyInfo = "<p>Packages: </p><p>${pkg.pkgInfo.pkgfile}</p>"
    String bodyRepo = "<p><strong>${msg}</strong></p>"
    String bodyAuthor = "<p>authorName: ${pkg.authorInfo.name}</p><p>authorEmail: ${pkg.authorInfo.email}</p>"
    String bodyUrl = "<p><a href=${BUILD_URL}>${BUILD_URL}</a></p>"
    String sendTo = 'artix-builds@artixlinux.org'

    if ( pkg.repoAdd ) {
        sendMail = true
        subject = "[${pkg.repoAdd}] ${msgSubject}"
        bodyAction = "<p>Repo: ${pkg.repoAdd}</p>"
    } else if ( pkg.repoRemove ) {
        if ( ! pkg.isBuildSuccess ) {
            sendMail = true
            subject = "[${pkg.repoRemove}] ${msgSubject}"
            bodyAction = "<p>Repo: ${pkg.repoRemove}</p>"
        }
    } else {
        if ( msg == 'Failure' ) {
            sendMail = true
            isLogAttach = true
            subject = "[${pkg.repoAdd}] ${msgSubject}"
            bodyAction = "<p>Build: ${pkg.repoPathGit}</p>"
            sendTo = 'artix-build-failures@artixlinux.org'
        }
    }

    String body = "${bodyRepo}${bodyAction}${bodyInfo}${bodyAuthor}${bodyUrl}"

    if ( sendMail ) {
        if ( ! params.isDryRun ) {
            emailext (
                body: body,
                subject: subject,
                to: sendTo,
                attachLog: isLogAttach
            )
        }
    }
}
