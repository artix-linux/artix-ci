#!/usr/bin/env groovy

def call(def pkg) {
    String agentYaml = '.artixlinux/agent.yaml'
    if ( fileExists(agentYaml) ) {
        def data = readYaml file: agentYaml
        echo "agentLabelFile: ${data.label}"

        if ( data.label == 'slave' ){
            def nodesOnline = nodesByLabel label: data.label
            if ( nodesOnline.size() > 0 ) {
                pkg.agentLabel = data.label
            }
        }
    }
}
