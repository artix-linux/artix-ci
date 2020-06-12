#!/usr/bin/env groovy

def call(def pkg) {
    String msg = 'Failure'

    String msgSubject = "${msg}: ${pkg.info.pkgbase.name}"
    String subject = "[${pkg.config.src.repoAddName}] ${msgSubject}"
    String bodyAction = "<p>Build: ${pkg.config.src.repoPath}</p>"
    String bodyInfo = "<p>Packages: </p><p>${pkg.info.files.join('\n')}</p>"
    String bodyRepo = "<p><strong>${msg}</strong></p>"
    String bodyAuthor = "<p>authorName: ${pkg.author.name}</p><p>authorEmail: ${pkg.author.email}</p>"
    String bodyUrl = "<p><a href=${BUILD_URL}>${BUILD_URL}</a></p>"
    String sendTo = 'artix-build-failures@artixlinux.org'

    String body = "${bodyRepo}${bodyAction}${bodyInfo}${bodyAuthor}${bodyUrl}"

    emailext (body: body, subject: subject, to: sendTo, attachLog: true)
}
