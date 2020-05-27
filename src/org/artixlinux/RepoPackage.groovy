#!/usr/bin/env groovy

package org.artixlinux
class RepoPackage implements Serializable {

    def steps

    RepoPackage(steps) {
        this.steps = steps
    }

    private Map artixConfig = [:]

    private Map pkgInfo = [:]

    private Map authorInfo = [:]

    private List<String> repoListGit = []

    def getArtixConfig() {
        artixConfig
    }

    def getPkgInfo() {
        pkgInfo
    }

    def getAuthorInfo() {
        authorInfo
    }

    private String mapRepo(String name) {
        String repo = ''
        for ( int i = 0; i < artixConfig.repos.size(); i++ ) {
            if ( artixConfig.repos[i].arch.size() == 2 ) {
                if ( name == artixConfig.repos[i].arch[0] || name == artixConfig.repos[i].arch[1] ) {
                    repo = artixConfig.repos[i].name
                    break
                }
            } else {
                if ( name == artixConfig.repos[i].arch[0] ) {
                    repo = artixConfig.repos[i].name
                    break
                }
            }
        }
        return repo
    }

    private void repoPkgOp() {

        String srcRepo = repoListGit[0].path.tokenize('/')[1]

        if ( repoListGit[0].status == 'A' || repoListGit[0].status == 'M' ) {
            artixConfig.actions.isBuild = true
            artixConfig.tools.repoAddName = mapRepo(srcRepo)
            artixConfig.tools.repoName = artixConfig.tools.repoAddName
        } else if ( repoListGit[0].status == 'D' ) {
            artixConfig.actions.isRemove = true
            artixConfig.tools.repoRemoveName = mapRepo(srcRepo)
            artixConfig.tools.repoName = artixConfig.tools.repoRemoveName
        }

        if ( steps.fileExists(repoListGit[0].path + '/PKGBUILD') ) {
            artixConfig.tools.repoPathGit = repoListGit[0].path
        }

    }

    private void repoPkgMove(){

        String srcRepo = repoListGit[0].path.tokenize('/')[1]
        String destRepo = repoListGit[1].path.tokenize('/')[1]

        if ( repoListGit[0].status == 'M' ) {
            artixConfig.actions.isAdd = true
            artixConfig.tools.repoAddName = mapRepo(srcRepo)
            artixConfig.tools.repoPathGit = repoListGit[1].path
        } else if ( repoListGit[1].status == 'M' ) {
            artixConfig.actions.isAdd = true
            artixConfig.tools.repoAddName = mapRepo(destRepo)
            artixConfig.tools.repoPathGit = repoListGit[0].path
        }

        if ( repoListGit[0].status == 'D' ) {
            artixConfig.actions.isRemove = true
            artixConfig.tools.repoRemoveName = mapRepo(srcRepo)
            artixConfig.tools.repoPathGit = repoListGit[1].path
        } else if ( repoListGit[1].status == 'D' ) {
            artixConfig.actions.isRemove = true
            artixConfig.tools.repoRemoveName = mapRepo(destRepo)
            artixConfig.tools.repoPathGit = repoListGit[0].path
        }

        if ( repoListGit[0].status.contains('R') && repoListGit[1].status.contains('R') )  {
            artixConfig.actions.isAdd = true
            artixConfig.actions.isRemove = true
            artixConfig.tools.repoAddName = mapRepo(destRepo)
            artixConfig.tools.repoRemoveName = mapRepo(srcRepo)
            artixConfig.tools.repoPathGit = repoListGit[1].path
        }

        artixConfig.tools.repoName = artixConfig.tools.repoAddName
    }

    private void loadResources(){
        def conf = steps.libraryResource('org/artixlinux/artixConfig.yaml')
        artixConfig = steps.readYaml(text: conf)
    }

    private void loadChangeSet() {
        String gitCmd = 'git rev-parse @'
        String commit = steps.sh(returnStdout: true, script: gitCmd).trim()

        gitCmd = "git show -s --format='%an' ${commit}"
        String authorName = steps.sh(returnStdout: true, script: gitCmd)

        String authorGpg = "GPG_" + authorName.toUpperCase()

        gitCmd = "git show -s --format='%ae' ${commit}"
        String authorEmail = steps.sh(returnStdout: true, script: gitCmd)

        authorInfo = [name: authorName, email: authorEmail, gpgkey: authorGpg]

        gitCmd = "git show --pretty=format: --name-status ${commit}"
        List<String> changeSet = steps.sh(returnStdout: true, script: gitCmd).tokenize('\n')

        for ( int i = 0; i < changeSet.size(); i++ ) {
            List<String> entry = changeSet[i].split()
            String fileStatus = entry[0]
            for ( int j = 1; j < entry.size(); j++ ) {
                if ( entry[j].contains('/PKGBUILD') && entry[j].contains('repos/') ){
                    Map dataSet = [status: fileStatus, path: entry[j].minus('/PKGBUILD')]
                    repoListGit.add(dataSet)
                }
            }
        }
    }

    private void loadPkgYaml() {
        String pkgYaml = steps.sh(returnStdout: true, script: "${artixConfig.tools.yamlCmd} ${artixConfig.tools.repoPathGit}")
        pkgInfo = steps.readYaml(text: pkgYaml)
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
