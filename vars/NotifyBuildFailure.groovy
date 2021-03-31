#!/usr/bin/env groovy

def call(def pkg) {
    String path = pkg.config.src.repoPath
    String pkgbase = pkg.info.pkgbase.name

    String subject = "[${pkg.config.src.repoAddName}] Failure: ${pkgbase}"

    String body = """
        <p>Build: ${path}</p>
        <p>Name: ${pkgbase}</p>
        <p>author: ${pkg.config.author.name}</p>
        <p>email: ${pkg.config.author.email}</p>
        <p><a href=${BUILD_URL}>${BUILD_URL}</a></p>
        """

    emailext (
        attachmentsPattern: "**/*.log",
        mimeType: pkg.config.notify.mime,
        subject: subject,
        body: body,
        to: pkg.config.notify.fails,
        attachLog: true
    )

    archiveArtifacts(
        allowEmptyArchive: true,
        artifacts: "${path}/*.log"
    )
}
