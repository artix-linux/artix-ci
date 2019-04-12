#!/usr/bin/env groovy

def call(def pkg) {
    pkg.isBuildSuccess = true
    if ( pkg.agentLabel == 'slave' ) {
        sshPublisher(
            publishers: [
                sshPublisherDesc(
                    configName: orion,
                    transfers: [
                        sshTransfer(cleanRemote: false,
                        excludes: '', execCommand: '',
                        execTimeout: 120000,
                        flatten: false,
                        makeEmptyDirs: false,
                        noDefaultExcludes: false,
                        patternSeparator: '[, ]+',
                        remoteDirectory: '/srv/pkgpool',
                        remoteDirectorySDF: false,
                        removePrefix: '',
                        sourceFiles: '*.pkg.tar.xz')
                    ],
                    usePromotionTimestamp: false,
                    useWorkspaceInPromotion: false,
                    verbose: false
                )
            ]
        )
    }
}
