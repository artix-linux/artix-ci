#!/usr/bin/env groovy

def call(def pkg) {
    String repo = pkg.config.src.repoRemoveName

    String subject = "[${repo}] remove: ${pkg.info.pkgbase.name}-${pkg.info.version}"

    String body = """
        <p>Repo: ${repo}</p>
        <p>author: ${pkg.config.author.name}</p>
        <p>email: ${pkg.config.author.email}</p>
        <p>Packages:</p>
        """ + pkg.info.files.collect() { "<p>${it}</p>" }.join('\n')

    emailext (
        mimeType: pkg.config.notify.mime,
        subject: subject,
        body: body,
        to: pkg.config.notify.repos
    )
}
