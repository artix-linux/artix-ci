#!/usr/bin/env groovy

def call(def pkg) {
    pkg.postBuild()
    if ( pkg.agentLabel == 'slave' ) {
        sshPublisher(
            publishers: [
                sshPublisherDesc(
                    configName: pkg.agentLabel,
                    transfers: [
                        sshTransfer(cleanRemote: false,
                        excludes: '', execCommand: '',
                        execTimeout: 120000,
                        flatten: false,
                        makeEmptyDirs: false,
                        noDefaultExcludes: false,
                        patternSeparator: '[, ]+',
                        remoteDirectory: '/srv/pkg',
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
