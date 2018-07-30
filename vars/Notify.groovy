#!/usr/bin/env groovy

def call(String pkg) {

    def subject = "FAILURE CI: ${env.JOB_NAME}/${pkg}"
    def body = "<b>Failure</b><br><br>Project: ${env.JOB_NAME}/${pkg} <br>Build Number: ${env.BUILD_NUMBER} <br> URL of build: <a href=${env.BUILD_URL}></a>"

    emailext (
        body: body,
        subject: subject,
        to: "devs@artixlinux.org"
    )
}
