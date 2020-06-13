#!/usr/bin/env groovy

def call(def pkg) {
    String msg = 'Fixed'

    String subject = "[${pkg.config.src.repoAddName}] ${msg}: ${pkg.info.pkgbase.name}"
    String sendTo = 'artix-build-fixes@artixlinux.org'

    String body = """
        <p><strong>${msg}</strong></p>
        <p>Build: ${pkg.config.src.repoPath}</p>
        <p>Packages:</p>
        <p>${pkg.info.files.join('\n')}</p>
        <p>author: ${pkg.config.author.name}</p>
        <p>email: ${pkg.config.author.email}</p>
        <p><a href=${BUILD_URL}>${BUILD_URL}</a></p>
        """

    emailext (mimeType: 'text/html', body: body, subject: subject, to: sendTo, attachLog: false)
}
