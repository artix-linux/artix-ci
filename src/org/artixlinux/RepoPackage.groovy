#!/usr/bin/env groovy

package org.artixlinux
class RepoPackage implements Serializable {

    def steps

    RepoPackage(steps) {
        this.steps = steps
    }

    private Map artixRepos = [:]

    private Map artixTools = [:]

    private Map pkgInfo = [:]

    private Map pkgActions = [:]

    private Map authorInfo = [:]

    private Map jobInfo = [:]

    private List<String> repoListGit = []

    def getArtixRepos() {
        artixRepos
    }

    def getArtixTools() {
        artixTools
    }

    def getPkgActions() {
        pkgActions
    }

    def getPkgInfo() {
        pkgInfo
    }

    def getAuthorInfo() {
        authorInfo
    }

    def getJobInfo() {
        jobInfo
    }

    private Map mapRepo(String src) {
        Map repoMap = [:]
        if ( src == artixRepos.goblins.arch || src == artixRepos.goblins.any ) {
            repoMap << [src: artixRepos.goblins.name]
        } else if ( src == artixRepos.gremlins.arch || src == artixRepos.gremlins.any ) {
            repoMap << [src: artixRepos.gremlins.name]
        } else if ( src == artixRepos.system.arch || src == artixRepos.system.any ) {
            repoMap << [src: artixRepos.system.name]
        } else if ( src == artixRepos.world.arch || src == artixRepos.world.any ) {
            repoMap << [src: artixRepos.world.name]
        } else if ( src == artixRepos.galaxyGoblins.arch || src == artixRepos.galaxyGoblins.any ) {
            repoMap << [src: artixRepos.galaxyGoblins.name]
        } else if ( src == artixRepos.galaxyGremlins.arch || src == artixRepos.galaxyGremlins.any ) {
            repoMap << [src: artixRepos.galaxyGremlins.name]
        } else if ( src == artixRepos.galaxy.arch || src == artixRepos.galaxy.any ) {
            repoMap << [src: artixRepos.galaxy.name]
        } else if ( src == artixRepos.lib32Goblins.arch ) {
            repoMap << [src: artixRepos.lib32Goblins.name]
        } else if ( src == artixRepos.lib32Gremlins.arch ) {
            repoMap << [src: artixRepos.lib32Gremlins.name]
        } else if ( src == artixRepos.lib32.arch ) {
            repoMap << [src: artixRepos.lib32.name]
        } else if ( src == artixRepos.kdeWobble.arch || src == artixRepos.kdeWobble.any ) {
            repoMap << [src: artixRepos.kdeWobble.name]
        } else if ( src == artixRepos.gnomeWobble.arch || src == artixRepos.gnomeWobble.any ) {
            repoMap << [src: artixRepos.gnomeWobble.name]
        }
        return repoMap
    }

    private Map mapRepos(String src, String dest) {
        Map  repoMap = [:]
        if ( src == artixRepos.gremlins.arch && dest == artixRepos.goblins.arch ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.gremlins.name]
        } else if ( src == artixRepos.gremlins.any && dest == artixRepos.goblins.any ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.gremlins.name]
        } else if ( src.contains(artixRepos.galaxyGremlins.git) && dest.contains(artixRepos.galaxyGoblins.git) ) {
            repoMap << [src: artixRepos.galaxyGoblins.name, dest: artixRepos.galaxyGremlins.name]
        } else if ( src.contains(artixRepos.lib32Gremlins.git) && dest.contains(artixRepos.lib32Goblins.git) ) {
            repoMap << [src: artixRepos.lib32Goblins.name, dest: artixRepos.lib32Gremlins.name]
        } else if ( src.contains(artixRepos.system.git) && dest == artixRepos.gremlins.arch ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.system.name]
        } else if ( src.contains(artixRepos.system.git) && dest == artixRepos.gremlins.any ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.system.name]
        } else if ( src.contains(artixRepos.world.git) && dest == artixRepos.gremlins.arch ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.world.name]
        } else if ( src.contains(artixRepos.world.git) && dest == artixRepos.gremlins.any ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.world.name]
        } else if ( src == artixRepos.galaxy.arch && dest.contains(artixRepos.galaxyGremlins.git) ) {
            repoMap << [src: artixRepos.galaxyGremlins.name, dest: artixRepos.galaxy.name]
        } else if ( src == artixRepos.galaxy.any && dest.contains(artixRepos.galaxyGremlins.git) ) {
            repoMap << [src: artixRepos.galaxyGremlins.name, dest: artixRepos.galaxy.name]
        } else if ( src.contains(artixRepos.lib32.arch) && dest.contains(artixRepos.lib32Gremlins.git) ) {
            repoMap << [src: artixRepos.lib32Gremlins.name, dest: artixRepos.lib32.name]
        } else if ( src == artixRepos.goblins.arch && dest == artixRepos.gremlins.arch ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.goblins.name]
        } else if ( src == artixRepos.goblins.any && dest == artixRepos.gremlins.any ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.goblins.name]
        } else if ( src.contains(artixRepos.galaxyGoblins.git) && dest.contains(artixRepos.galaxyGremlins.git) ) {
            repoMap << [src: artixRepos.galaxyGremlins.name, dest: artixRepos.galaxyGoblins.name]
        } else if ( src.contains(artixRepos.lib32Goblins.git) && dest.contains(artixRepos.lib32Gremlins.git) ) {
            repoMap << [src: artixRepos.lib32Gremlins.name, dest: artixRepos.lib32Goblins.name]
        } else if ( src == artixRepos.gremlins.arch && dest.contains(artixRepos.system.git) ) {
            repoMap << [src: artixRepos.system.name, dest: artixRepos.gremlins.name]
        } else if ( src == artixRepos.gremlins.any && dest.contains(artixRepos.system.git) ) {
            repoMap << [src: artixRepos.system.name, dest: artixRepos.gremlins.name]
        } else if ( src == artixRepos.gremlins.arch && dest.contains(artixRepos.world.git) ) {
            repoMap << [src: artixRepos.world.name, dest: artixRepos.gremlins.name]
        } else if ( src == artixRepos.gremlins.any && dest.contains(artixRepos.world.git) ) {
            repoMap << [src: artixRepos.world.name, dest: artixRepos.gremlins.name]
        } else if ( src.contains(artixRepos.galaxyGremlins.git) && dest == artixRepos.galaxy.arch ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.galaxyGremlins.name]
        } else if ( src.contains(artixRepos.galaxyGremlins.git) && dest == artixRepos.galaxy.any ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.galaxyGremlins.name]
        } else if ( src.contains(artixRepos.lib32Gremlins.git) && dest.contains(artixRepos.lib32.arch) ) {
            repoMap << [src: artixRepos.lib32.name, dest: artixRepos.lib32Gremlins.name]
        } else if ( src.contains(artixRepos.world.git) && dest.contains(artixRepos.system.git) ) {
            repoMap << [src: artixRepos.system.name, dest: artixRepos.world.name]
        } else if ( src.contains(artixRepos.system.git) && dest.contains(artixRepos.world.git) ) {
            repoMap << [src: artixRepos.world.name, dest: artixRepos.system.name]
        } else if ( src == artixRepos.galaxy.arch && dest.contains(artixRepos.system.git) ) {
            repoMap << [src: artixRepos.system.name, dest: artixRepos.galaxy.name]
        } else if ( src == artixRepos.galaxy.any && dest.contains(artixRepos.system.git) ) {
            repoMap << [src: artixRepos.system.name, dest: artixRepos.galaxy.name]
        } else if ( src.contains(artixRepos.system.git) && dest == artixRepos.galaxy.arch ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.system.name]
        } else if ( src.contains(artixRepos.system.git) && dest == artixRepos.galaxy.any ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.system.name]
        } else if ( src == artixRepos.galaxy.arch && dest.contains(artixRepos.world.git) ) {
            repoMap << [src: artixRepos.world.name, dest: artixRepos.galaxy.name]
        } else if ( src == artixRepos.galaxy.any && dest.contains(artixRepos.world.git) ) {
            repoMap << [src: artixRepos.world.name, dest: artixRepos.galaxy.name]
        } else if ( src.contains(artixRepos.world.git) && dest == artixRepos.galaxy.arch ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.world.name]
        } else if ( src.contains(artixRepos.world.git) && dest == artixRepos.galaxy.any ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.world.name]
        } else if ( src.contains(artixRepos.galaxyGoblins.git) && dest == artixRepos.goblins.arch ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.galaxyGoblins.name]
        } else if ( src.contains(artixRepos.galaxyGoblins.git) && dest == artixRepos.goblins.any ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.galaxyGoblins.name]
        } else if ( src  == artixRepos.goblins.arch && dest.contains(artixRepos.galaxyGoblins.git) ) {
            repoMap << [src: artixRepos.galaxyGoblins.name, dest: artixRepos.goblins.name]
        } else if ( src  == artixRepos.goblins.any && dest.contains(artixRepos.galaxyGoblins.git) ) {
            repoMap << [src: artixRepos.galaxyGoblins.name, dest: artixRepos.goblins.name]
        } else if ( src.contains(artixRepos.galaxyGremlins.git) && dest == artixRepos.gremlins.arch ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.galaxyGremlins.name]
        } else if ( src.contains(artixRepos.galaxyGremlins.git) && dest == artixRepos.gremlins.any ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.galaxyGremlins.name]
        } else if ( src  == artixRepos.gremlins.arch && dest.contains(artixRepos.galaxyGremlins.git) ) {
            repoMap << [src: artixRepos.galaxyGremlins.name, dest: artixRepos.gremlins.name]
        } else if ( src  == artixRepos.gremlins.any && dest.contains(artixRepos.galaxyGremlins.git) ) {
            repoMap << [src: artixRepos.galaxyGremlins.name, dest: artixRepos.gremlins.name]
        } else if ( src  == artixRepos.goblins.arch && dest == artixRepos.kdeWobble.arch ) {
            repoMap << [src: artixRepos.kdeWobble.name, dest: artixRepos.goblins.name]
        } else if ( src  == artixRepos.goblins.any && dest == artixRepos.kdeWobble.any ) {
            repoMap << [src: artixRepos.kdeWobble.name, dest: artixRepos.goblins.name]
        } else if ( src  == artixRepos.goblins.arch && dest == artixRepos.gnomeWobble.arch ) {
            repoMap << [src: artixRepos.gnomeWobble.name, dest: artixRepos.goblins.name]
        } else if ( src  == artixRepos.goblins.any && dest == artixRepos.gnomeWobble.any ) {
            repoMap << [src: artixRepos.gnomeWobble.name, dest: artixRepos.goblins.name]
        } else if ( src  == artixRepos.kdeWobble.arch && dest == artixRepos.goblins.arch ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.kdeWobble.name]
        } else if ( src  == artixRepos.kdeWobble.any && dest == artixRepos.goblins.any ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.kdeWobble.name]
        } else if ( src  == artixRepos.gnomeWobble.arch && dest == artixRepos.goblins.arch ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.gnomeWobble.name]
        } else if ( src  == artixRepos.gnomeWobble.any && dest == artixRepos.goblins.any ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.gnomeWobble.name]
        }
        return repoMap
    }

    private void repoPkgOp() {

        String srcRepo = repoListGit[0].path.tokenize('/')[1]

        if ( repoListGit[0].status == 'A' || repoListGit[0].status == 'M' ) {
            pkgActions.isBuild = true
            artixTools.repoAddName = mapRepo(srcRepo).src
            artixTools.repoName = artixTools.repoAddName
        } else if ( repoListGit[0].status == 'D' ) {
            pkgActions.isRemove = true
            artixTools.repoRemoveName = mapRepo(srcRepo).src
            artixTools.repoName = artixTools.repoRemoveName
        }

        if ( steps.fileExists(repoListGit[0].path + '/PKGBUILD') ) {
            artixTools.repoPathGit = repoListGit[0].path
        }

    }

    private void repoPkgMove(){

        String srcRepo = repoListGit[0].path.tokenize('/')[1]
        String destRepo = repoListGit[1].path.tokenize('/')[1]

        if ( repoListGit[0].status == 'M' ) {
            pkgActions.isAdd = true
            artixTools.repoAddName = mapRepo(srcRepo).src
            artixTools.repoPathGit = repoListGit[1].path
        } else if ( repoListGit[1].status == 'M' ) {
            pkgActions.isAdd = true
            artixTools.repoAddName = mapRepo(destRepo).src
            artixTools.repoPathGit = repoListGit[0].path
        }

        if ( repoListGit[0].status == 'D' ) {
            pkgActions.isRemove = true
            artixTools.repoRemoveName = mapRepo(srcRepo).src
            artixTools.repoPathGit = repoListGit[1].path
        } else if ( repoListGit[1].status == 'D' ) {
            pkgActions.isRemove = true
            artixTools.repoRemoveName = mapRepo(destRepo).src
            artixTools.repoPathGit = repoListGit[0].path
        }

        if ( repoListGit[0].status.contains('R') && repoListGit[1].status.contains('R') )  {
            pkgActions.isAdd = true
            pkgActions.isRemove = true
            artixTools.repoAddName = mapRepos(srcRepo, destRepo).src
            artixTools.repoRemoveName = mapRepos(srcRepo, destRepo).dest
            artixTools.repoPathGit = repoListGit[1].path
        }

        artixTools.repoName = artixTools.repoAddName
    }

    private void loadResources(){
        def repos = steps.libraryResource('org/artixlinux/repos/artixRepos.yaml')
        artixRepos = steps.readYaml(text: repos)

        def conf = steps.libraryResource('org/artixlinux/tools/artixTools.yaml')
        artixTools = steps.readYaml(text: conf)

        def actions = steps.libraryResource('org/artixlinux/actions/artixActions.yaml')
        pkgActions = steps.readYaml(text: actions)
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

        String pkgYaml = steps.sh(returnStdout: true, script: "${artixTools.yamlCmd} ${artixTools.repoPathGit}")

        pkgInfo = steps.readYaml(text: pkgYaml)

        jobInfo << [name: "${artixTools.repoName}", desc: "${pkgInfo.pkgbase.pkgname}-${pkgInfo.pkgbase.fullver}"]
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
