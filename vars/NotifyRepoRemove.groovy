#!/usr/bin/env groovy

def call(def pkg) {
    String msg = 'repo-remove'

    String msgSubject = "${msg}: ${pkg.info.pkgbase.name}"
    String subject = "[${pkg.config.src.repoRemoveName}] ${msgSubject}"
    String bodyAction = "<p>Repo: ${pkg.config.src.repoRemoveName}</p>"
    String bodyInfo = "<p>Packages: </p><p>${pkg.info.files.join('\n')}</p>"
    String bodyRepo = "<p><strong>${msg}</strong></p>"
    String bodyAuthor = "<p>authorName: ${pkg.author.name}</p><p>authorEmail: ${pkg.author.email}</p>"
    String bodyUrl = "<p><a href=${BUILD_URL}>${BUILD_URL}</a></p>"
    String sendTo = 'artix-builds@artixlinux.org'

    String body = "${bodyRepo}${bodyAction}${bodyInfo}${bodyAuthor}${bodyUrl}"

    emailext (body: body, subject: subject, to: sendTo, attachLog: false)
}
