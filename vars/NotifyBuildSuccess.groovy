#!/usr/bin/env groovy

def call(def pkg) {
    sh "${pkg.config.tools.cmdSign}"
    pkg.config.actions.isAdd = true

    String path = pkg.config.src.repoPath
    String pkgbase = "${pkg.info.pkgbase.name}-${pkg.info.version}"

    String subject = "[${pkg.config.src.repoAddName}] Success: ${pkgbase}"

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
        to: pkg.config.notify.builds,
        attachLog: false
    )

    archiveArtifacts(
        allowEmptyArchive: true,
        artifacts: "${path}/*.log"
    )
}
