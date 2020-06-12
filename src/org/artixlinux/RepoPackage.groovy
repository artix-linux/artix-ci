#!/usr/bin/env groovy

package org.artixlinux
class RepoPackage implements Serializable {

    def steps

    RepoPackage(steps) {
        this.steps = steps
    }

    private Map config = [:]

    private Map info = [:]

    private Map author = [:]

    private List<String> repoListGit = []

    private Boolean isNextLayout = false

    def getConfig() {
        config
    }

    def getInfo() {
        info
    }

    def getAuthor() {
        author
    }

    private String findRepo(String name) {
        if ( isNextLayout ) {
            return config.repos.each { it }.find { it.vcs == name }.name
        } else {
            return config.repos.each { it }.find { it.arch == name || it.any == name }.name
        }
    }

    private void repoPkgOp() {

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

    private void repoPkgMove(){

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

    private void loadResources(){
        def conf = steps.libraryResource('org/artixlinux/artixConfig.yaml')
        config = steps.readYaml(text: conf)
    }

    private void loadChangeSet() {
        String gitCmd = 'git rev-parse @'
        String commit = steps.sh(returnStdout: true, script: gitCmd).trim()

        gitCmd = "git show -s --format='%an' ${commit}"
        String authorName = steps.sh(returnStdout: true, script: gitCmd)

        String authorGpg = "GPG_" + authorName.toUpperCase()

        gitCmd = "git show -s --format='%ae' ${commit}"
        String authorEmail = steps.sh(returnStdout: true, script: gitCmd)

        author = [name: authorName, email: authorEmail, gpgkey: authorGpg]

        gitCmd = "git show --pretty=format: --name-status ${commit}"
        List<String> changeSet = steps.sh(returnStdout: true, script: gitCmd).tokenize('\n')

        for ( int i = 0; i < changeSet.size(); i++ ) {
            List<String> entry = changeSet[i].split()
            String fileStatus = entry[0]
            for ( int j = 1; j < entry.size(); j++ ) {
                if ( entry[j].startsWith(config.arch + "/") && entry[j].endsWith(config.pkgbuild) ) {
                    isNextLayout = true
                    Map dataSet = [status: fileStatus, path: entry[j].minus(config.pkgbuild)]
                    repoListGit.add(dataSet)
                } else if ( entry[j].startsWith('repos/') && entry[j].endsWith(config.pkgbuild) ) {
                    Map dataSet = [status: fileStatus, path: entry[j].minus(config.pkgbuild)]
                    repoListGit.add(dataSet)
                }
            }
        }
    }

    private void loadPkgYaml() {
        String pkgYaml = steps.sh(returnStdout: true, script: "${config.tools.cmdYaml} ${config.src.repoPath}")
        info = steps.readYaml(text: pkgYaml)
    }

    void initialize() {

        loadResources()

        loadChangeSet()

        byte repoCount = repoListGit.size()

        if ( repoCount > 0 ) {

            if ( repoCount == 1 ) {

                repoPkgOp()

            } else if ( repoCount == 2 ) {

                repoPkgMove()
            }
        }
        loadPkgYaml()
    }
}
