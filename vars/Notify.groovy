#!/usr/bin/env groovy

def call(String pkg) {

    def subject = "FAILURE CI: ${JOB_NAME}/${pkg}"
    def body = "Failure: ${JOB_NAME}/${pkg} Build Number: ${BUILD_NUMBER} URL: ${BUILD_URL}"

    emailext (
        body: body,
        subject: subject,
        to: "devs@artixlinux.org"
    )
}
