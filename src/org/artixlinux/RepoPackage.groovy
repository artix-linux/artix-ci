#!/usr/bin/env groovy

package org.artixlinux
class RepoPackage implements Serializable {

    def steps

    RepoPackage(steps) {
        this.steps = steps
    }

    private Map config = [:]

    private Map info = [:]

    private List<String> repoListGit = []

    def getConfig() {
        config
    }

    def getInfo() {
        info
    }

    private String findRepo(String name) {
        if ( config.src.isNextLayout ) {
            return config.repos.each { it }.find { it.vcs == name }.name
        } else {
            return config.repos.each { it }.find { it.arch == name || it.any == name }.name
        }
    }

    private void configureOperation() {

        String srcRepo = repoListGit[0].path.tokenize('/')[1]

        if ( repoListGit[0].status == 'A' || repoListGit[0].status == 'M' ) {
            config.actions.isBuild = true
            config.src.repoAddName = findRepo(srcRepo)
            config.src.repoName = config.src.repoAddName
        } else if ( repoListGit[0].status == 'D' ) {
            config.actions.isRemove = true
            config.src.repoRemoveName = findRepo(srcRepo)
            config.src.repoName = config.src.repoRemoveName
        }

        if ( steps.fileExists(repoListGit[0].path + config.pkgbuild) ) {
            config.src.repoPath = repoListGit[0].path
        }

    }

    private void configureOperations(){

        String srcRepo = repoListGit[0].path.tokenize('/')[1]
        String destRepo = repoListGit[1].path.tokenize('/')[1]

        if ( repoListGit[0].status == 'M' ) {
            config.actions.isAdd = true
            config.src.repoAddName = findRepo(srcRepo)
            config.src.repoPath = repoListGit[1].path
        } else if ( repoListGit[1].status == 'M' ) {
            config.actions.isAdd = true
            config.src.repoAddName = findRepo(destRepo)
            config.src.repoPath = repoListGit[0].path
        }

        if ( repoListGit[0].status == 'D' ) {
            config.actions.isRemove = true
            config.src.repoRemoveName = findRepo(srcRepo)
            config.src.repoPath = repoListGit[1].path
        } else if ( repoListGit[1].status == 'D' ) {
            config.actions.isRemove = true
            config.src.repoRemoveName = findRepo(destRepo)
            config.src.repoPath = repoListGit[0].path
        }

        if ( repoListGit[0].status.contains('R') && repoListGit[1].status.contains('R') )  {
            config.actions.isAdd = true
            config.actions.isRemove = true
            config.src.repoAddName = findRepo(destRepo)
            config.src.repoRemoveName = findRepo(srcRepo)
            config.src.repoPath = repoListGit[1].path
        }

        config.src.repoName = config.src.repoAddName
    }

    private void loadConfig(){
        def conf = steps.libraryResource('org/artixlinux/config.yaml')
        config = steps.readYaml(text: conf)
    }

    private void loadChangeSet(String commit) {
        String cmdGit = "git show -s --format='%an' ${commit}"
        config.author.name = steps.sh(returnStdout: true, script: cmdGit)

        cmdGit = "git show -s --format='%ae' ${commit}"
        config.author.email = steps.sh(returnStdout: true, script: cmdGit)

        config.author.gpgkey = "GPG_" + config.author.name.toUpperCase()

        cmdGit = "git show --pretty=format: --name-status ${commit}"
        List<String> changeSet = steps.sh(returnStdout: true, script: cmdGit).tokenize('\n')

        for ( int i = 0; i < changeSet.size(); i++ ) {

            List<String> entry = changeSet[i].split()

            String fileStatus = entry[0]

            for ( int j = 1; j < entry.size(); j++ ) {

                if ( entry[j].startsWith(config.arch) && entry[j].endsWith(config.pkgbuild) ) {
                    config.src.isNextLayout = true
                    Map dataSet = [status: fileStatus, path: entry[j].minus(config.pkgbuild)]
                    repoListGit.add(dataSet)
                } else if ( entry[j].startsWith('repos/') && entry[j].endsWith(config.pkgbuild) ) {
                    Map dataSet = [status: fileStatus, path: entry[j].minus(config.pkgbuild)]
                    repoListGit.add(dataSet)
                }

            }
        }
    }

    private void configureTools() {
        config.tools.cmdYaml += " ${config.src.repoPath}"
        String srcInfo = steps.sh(returnStdout: true, script: "${config.tools.cmdYaml}")
        info = steps.readYaml(text: srcInfo)

        String fileArgs = info.files.join(' ')
        String pkgBaseArgs = info.packages.collect { it.pkgname }.join(' ')

        config.tools.cmdBuild += " -d ${config.src.repoAddName}"
        config.tools.cmdSign += " ${fileArgs}"
        config.tools.cmdRepoAdd += " -d ${config.src.repoAddName} ${fileArgs}"
        config.tools.cmdRepoRemove += " -d ${config.src.repoRemoveName} ${pkgBaseArgs}"
    }

    void initialize(String commit) {

        loadConfig()

        loadChangeSet(commit)

        byte repoCount = repoListGit.size()

        if ( repoCount > 0 ) {

            if ( repoCount == 1 ) {
                configureOperation()
            } else if ( repoCount == 2 ) {
                configureOperations()
            }

        }
        configureTools()
    }

}
