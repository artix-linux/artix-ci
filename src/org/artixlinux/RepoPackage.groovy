#!/usr/bin/env groovy

package org.artixlinux
class RepoPackage implements Serializable {
    private final Script script
    private String pkgRepo = ''
    private String agentLabel = 'master'
    private List<String> addArgs = ['deploypkg', '-a', '-d']
    private List<String> rmArgs = ['deploypkg', '-r', '-d']
    private List<String> buildArgs = ['buildpkg', '-r']
    private Boolean isAdd = false
    private Boolean isRemove = false
    private Boolean isBuild = false
    private Boolean isBuildSuccess = false
    private List<String> repoList = []
    private String repoAdd = ''
    private String repoRemove = ''
    private List<String> pkgName = []

    private Map artixRepos = [
        system: [name: 'system', git: 'core', arch: 'core-x86_64', any: 'core-any'],
        world: [name: 'world', git: 'extra', arch: 'extra-x86_64', any: 'extra-any'],
        gremlins: [name: 'gremlins', git: 'testing', arch: 'testing-x86_64', any: 'testing-any'],
        goblins: [name: 'goblins', git: 'staging', arch: 'staging-x86_64', any: 'staging-any'],
        galaxy: [name: 'galaxy', git: 'community', arch: 'community-x86_64', any: 'community-any'],
        galaxyGremlins: [name: 'galaxy-gremlins', git: 'community-testing', arch: 'community-testing-x86_64', any: 'community-testing-any'],
        galaxyGoblins: [name: 'galaxy-goblins', git: 'community-staging', arch: 'community-staging-x86_64', any: 'community-staging-any'],
        lib32: [name: 'lib32', git: 'multilib', arch: 'multilib-x86_64'],
        lib32Gremlins: [name: 'lib32-gremlins', git: 'multilib-testing', arch: 'multilib-testing-x86_64'],
        lib32Goblins: [name: 'lib32-goblins', git: 'multilib-staging', arch: 'multilib-staging-x86_64']
    ]

    RepoPackage(Script script) {
        this.script = script
    }

    def getPkgRepo() {
        pkgRepo
    }

    def getAgentLabel() {
        agentLabel
    }
    def setAgentLabel(value) {
        agentLabel = value
    }

    def getPkgName() {
        pkgName
    }
    def setPkgName(value) {
        pkgName = value
    }

    def getRepoList() {
        repoList
    }
    def setRepoList(value) {
        repoList = value
    }

    def getAddArgs() {
        addArgs
    }

    def getRmArgs() {
        rmArgs
    }

    def getRepoAdd() {
        repoAdd
    }

    def getRepoRemove() {
        repoRemove
    }

    def getBuildArgs() {
        buildArgs
    }

    def getIsAdd() {
        isAdd
    }

    def getIsRemove() {
        isRemove
    }

    def getIsBuild() {
        isBuild
    }

    def getIsBuildSuccess() {
        isBuildSuccess
    }

    void postBuild() {
        isBuildSuccess = true
        addArgs.add('-s')
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
        }
        return repoMap
    }

    private Map mapRepos(String src, String dest) {
        Map  repoMap = [:]
        if ( src == artixRepos.goblins.arch && dest == artixRepos.gremlins.arch ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.goblins.name]
        } else if ( src == artixRepos.goblins.any && dest == artixRepos.gremlins.any ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.goblins.name]
        } else if ( src == artixRepos.gremlins.arch && dest == artixRepos.goblins.arch ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.gremlins.name]
        } else if ( src == artixRepos.gremlins.any && dest == artixRepos.goblins.any ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.gremlins.name]
        } else if ( src.contains(artixRepos.system.git) && dest == artixRepos.gremlins.arch ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.system.name]
        } else if ( src.contains(artixRepos.system.git) && dest == artixRepos.gremlins.any ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.system.name]
        } else if ( src == artixRepos.gremlins.arch && dest.contains(artixRepos.system.git) ) {
            repoMap << [src: artixRepos.system.name, dest: artixRepos.gremlins.name]
        } else if ( src == artixRepos.gremlins.any && dest.contains(artixRepos.system.git) ) {
            repoMap << [src: artixRepos.system.name, dest: artixRepos.gremlins.name]
        } else if ( src.contains(artixRepos.world.git) && dest == artixRepos.gremlins.arch ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.world.name]
        } else if ( src.contains(artixRepos.world.git) && dest == artixRepos.gremlins.any ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.world.name]
        } else if ( src == artixRepos.gremlins.arch && dest.contains(artixRepos.world.git) ) {
            repoMap << [src: artixRepos.world.name, dest: artixRepos.gremlins.name]
        } else if ( src == artixRepos.gremlins.any && dest.contains(artixRepos.world.git) ) {
            repoMap << [src: artixRepos.world.name, dest: artixRepos.gremlins.name]
        } else if ( src.contains(artixRepos.system.git) && dest.contains(artixRepos.world.git) ) {
            repoMap << [src: artixRepos.world.name, dest: artixRepos.system.name]
        } else if ( src.contains(artixRepos.world.git) && dest.contains(artixRepos.system.git) ) {
            repoMap << [src: artixRepos.system.name, dest: artixRepos.world.name]
        } else if ( src == artixRepos.galaxy.arch && dest.contains(artixRepos.world.git) ) {
            repoMap << [src: artixRepos.world.name, dest: artixRepos.galaxy.name]
        } else if ( src == artixRepos.galaxy.any && dest.contains(artixRepos.world.git) ) {
            repoMap << [src: artixRepos.world.name, dest: artixRepos.galaxy.name]
        } else if ( src.contains(artixRepos.world.git) && dest == artixRepos.galaxy.arch ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.world.name]
        } else if ( src.contains(artixRepos.world.git) && dest == artixRepos.galaxy.any ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.world.name]
        } else if ( src == artixRepos.galaxy.arch && dest.contains(artixRepos.system.git) ) {
            repoMap << [src: artixRepos.system.name, dest: artixRepos.galaxy.name]
        } else if ( src == artixRepos.galaxy.any && dest.contains(artixRepos.system.git) ) {
            repoMap << [src: artixRepos.system.name, dest: artixRepos.galaxy.name]
        } else if ( src.contains(artixRepos.system.git) && dest == artixRepos.galaxy.arch ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.system.name]
        } else if ( src.contains(artixRepos.system.git) && dest == artixRepos.galaxy.any ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.system.name]
        } else if ( src.contains(artixRepos.galaxyGoblins.git) && dest.contains(artixRepos.galaxyGremlins.git) ) {
            repoMap << [src: artixRepos.galaxyGremlins.name, dest: artixRepos.galaxyGoblins.name]
        } else if ( src.contains(artixRepos.galaxyGremlins.git) && dest.contains(artixRepos.galaxyGoblins.git) ) {
            repoMap << [src: artixRepos.galaxyGoblins.name, dest: artixRepos.galaxyGremlins.name]
        } else if ( src.contains(artixRepos.galaxyGremlins.git) && dest == artixRepos.galaxy.arch ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.galaxyGremlins.name]
        } else if ( src.contains(artixRepos.galaxyGremlins.git) && dest == artixRepos.galaxy.any ) {
            repoMap << [src: artixRepos.galaxy.name, dest: artixRepos.galaxyGremlins.name]
        } else if ( src == artixRepos.galaxy.arch && dest.contains(artixRepos.galaxyGremlins.git) ) {
            repoMap << [src: artixRepos.galaxyGremlins.name, dest: artixRepos.galaxy.name]
        } else if ( src == artixRepos.galaxy.any && dest.contains(artixRepos.galaxyGremlins.git) ) {
            repoMap << [src: artixRepos.galaxyGremlins.name, dest: artixRepos.galaxy.name]
        } else if ( src.contains(artixRepos.lib32Goblins.git) && dest.contains(artixRepos.lib32Gremlins.git) ) {
            repoMap << [src: artixRepos.lib32Gremlins.name, dest: artixRepos.lib32Goblins.name]
        } else if ( src.contains(artixRepos.lib32Gremlins.git) && dest.contains(artixRepos.lib32Goblins.git) ) {
            repoMap << [src: artixRepos.lib32Goblins.name, dest: artixRepos.lib32Gremlins.name]
        } else if ( src.contains(artixRepos.lib32Gremlins.git) && dest.contains(artixRepos.lib32.arch) ) {
            repoMap << [src: artixRepos.lib32.name, dest: artixRepos.lib32Gremlins.name]
        } else if ( src.contains(artixRepos.lib32.arch) && dest.contains(artixRepos.lib32Gremlins.git) ) {
            repoMap << [src: artixRepos.lib32Gremlins.name, dest: artixRepos.lib32.name]
        } else if ( src.contains(artixRepos.galaxyGremlins.git) && dest == artixRepos.gremlins.arch ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.galaxyGremlins.name]
        } else if ( src.contains(artixRepos.galaxyGremlins.git) && dest == artixRepos.gremlins.any ) {
            repoMap << [src: artixRepos.gremlins.name, dest: artixRepos.galaxyGremlins.name]
        } else if ( src  == artixRepos.gremlins.arch && dest.contains(artixRepos.galaxyGremlins.git) ) {
            repoMap << [src: artixRepos.galaxyGremlins.name, dest: artixRepos.gremlins.name]
        } else if ( src  == artixRepos.gremlins.any && dest.contains(artixRepos.galaxyGremlins.git) ) {
            repoMap << [src: artixRepos.galaxyGremlins.name, dest: artixRepos.gremlins.name]
        } else if ( src.contains(artixRepos.galaxyGoblins.git) && dest == artixRepos.goblins.arch ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.galaxyGoblins.name]
        } else if ( src.contains(artixRepos.galaxyGoblins.git) && dest == artixRepos.goblins.any ) {
            repoMap << [src: artixRepos.goblins.name, dest: artixRepos.galaxyGoblins.name]
        } else if ( src  == artixRepos.goblins.arch && dest.contains(artixRepos.galaxyGoblins.git) ) {
            repoMap << [src: artixRepos.galaxyGoblins.name, dest: artixRepos.goblins.name]
        } else if ( src  == artixRepos.goblins.any && dest.contains(artixRepos.galaxyGoblins.git) ) {
            repoMap << [src: artixRepos.galaxyGoblins.name, dest: artixRepos.goblins.name]
        }
        return repoMap
    }

    void initialize() {

        byte repoCount = repoList.size()

        if ( repoCount > 0 ) {

            String srcRepo = repoList[0].path.tokenize('/')[1]

            if ( repoCount == 1 ) {

                if ( repoList[0].status == 'A' || repoList[0].status == 'M' ) {
                    isBuild = true
                    repoAdd = mapRepo(srcRepo).src
                    buildArgs.add(repoAdd)
                } else if ( repoList[0].status == 'D' ) {
                    isRemove = true
                    repoRemove = mapRepo(srcRepo).src
                }
                pkgRepo = repoList[0].path

            } else if ( repoCount == 2 ) {

                String destRepo = repoList[1].path.tokenize('/')[1]

                if ( repoList[0].status == 'M' ) {
                    isAdd = true
                    repoAdd = mapRepo(srcRepo).src
                    pkgRepo = repoList[1].path
                } else if ( repoList[1].status == 'M' ) {
                    isAdd = true
                    repoAdd = mapRepo(destRepo).src
                    pkgRepo = repoList[0].path
                }

                if ( repoList[0].status == 'D' ) {
                    isRemove = true
                    repoRemove = mapRepo(srcRepo).src
                    pkgRepo = repoList[1].path
                } else if ( repoList[1].status == 'D' ) {
                    isRemove = true
                    repoRemove = mapRepo(destRepo).src
                    pkgRepo = repoList[0].path
                }

                if ( repoList[0].status.contains('R') && repoList[1].status.contains('R') )  {
                    isAdd = true
                    isRemove = true
                    repoAdd = mapRepos(srcRepo, destRepo).src
                    repoRemove = mapRepos(srcRepo, destRepo).dest
                    pkgRepo = repoList[1].path
                }
            }
            addArgs.add(repoAdd)
            rmArgs.add(repoRemove)
        }
    }
}
