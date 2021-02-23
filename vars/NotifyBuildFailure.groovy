#!/usr/bin/env groovy

def call(def pkg) {
    archiveArtifacts(
        allowEmptyArchive: true,
        artifacts: "${pkg.config.arch}**/*.log"
    )

    String msg = 'Failure'
    String subject = "[${pkg.config.src.repoAddName}] ${msg}: ${pkg.info.pkgbase.name}"

    String body = """
        <p><strong>${msg}</strong></p>
        <p>Build: ${pkg.config.src.repoPath}</p>
        <p>Name:</p>
        <p>${pkg.info.pkgbase.name}</p>
        <p>author: ${pkg.config.author.name}</p>
        <p>email: ${pkg.config.author.email}</p>
        <p><a href=${BUILD_URL}>${BUILD_URL}</a></p>
        """

    emailext (
        mimeType: pkg.config.notify.mime,
        subject: subject,
        body: body,
        to: pkg.config.notify.fails,
        attachLog: false,
        attachmentsPattern: "*.log",
        compressLog: true
    )
}
