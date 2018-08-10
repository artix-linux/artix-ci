#!/usr/bin/env groovy

def call(String pkg) {

    def subject = "FAILURE CI: ${env.JOB_NAME}/${pkg}"
    def body = "<b>Failure</b><br><br>Project: ${JOB_NAME}/${pkg} <br>Build Number: ${BUILD_NUMBER} <br> URL of build: <a href=${BUILD_URL}></a>"

    emailext (
        body: body,
        subject: subject,
        to: "devs@artixlinux.org"
    )
}
