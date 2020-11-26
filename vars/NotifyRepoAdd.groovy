#!/usr/bin/env groovy

def call(def pkg) {
    String msg = 'repo-add'

    String subject = "[${pkg.config.src.repoAddName}] ${msg}: ${pkg.info.pkgbase.name}"

    String body = """
        <p><strong>${msg}</strong></p>
        <p>Repo: ${pkg.config.src.repoAddName}</p>
        <p>Packages:</p>
        <p>${pkg.info.files.join('\n')}</p>
        <p>author: ${pkg.config.author.name}</p>
        <p>email: ${pkg.config.author.email}</p>
        <p><a href=${BUILD_URL}>${BUILD_URL}</a></p>
        """

    emailext (
        mimeType: 'text/html',
        body: body,
        subject: subject,
        to: pkg.config.notify.builds,
        attachLog: false
    )
}
