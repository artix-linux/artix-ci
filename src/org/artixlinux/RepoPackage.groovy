#!/usr/bin/env groovy

package org.artixlinux
class RepoPackage implements Serializable {
    private final Script script
    private String pkgTrunk = ''
    private String pkgRepo = ''
    private List<String> addArgs = ['deploypkg', '-a', '-d']
    private List<String> rmArgs = ['deploypkg', '-r', '-d']
    private List<String> buildArgs = ['buildpkg', '-r']
    private Boolean isAdd = false
    private Boolean isRemove = false
    private Boolean isBuild = false
    private Boolean isBuildSuccess = false

    private Map systemRepo = [artixName: 'system', archName: 'core', arch: 'core-x86_64', any: 'core-any']
    private Map worldRepo = [artixName: 'world', archName: 'extra', arch: 'extra-x86_64', any: 'extra-any']
    private Map gremlinsRepo = [artixName: 'gremlins', archName: 'testing', arch: 'testing-x86_64', any: 'testing-any']
    private Map goblinsRepo = [artixName: 'goblins', archName: 'staging', arch: 'staging-x86_64', any: 'staging-any']

    private Map galaxyRepo = [artixName: 'galaxy', archName: 'community', arch: 'community-x86_64', any: 'community-any']
    private Map galaxyGremlinsRepo = [artixName: 'galaxy-gremlins', archName: 'community-testing', arch: 'community-testing-x86_64', any: 'community-testing-any']
    private Map galaxyGoblinsRepo = [artixName: 'galaxy-goblins', archName: 'community-staging', arch: 'community-staging-x86_64', any: 'community-staging-any']

    private Map lib32Repo = [artixName: 'lib32', archName: 'multilib', arch: 'multilib-x86_64']
    private Map lib32GremlinsRepo = [artixName: 'lib32-gremlins', archName: 'multilib-testing', arch: 'multilib-testing-x86_64']
    private Map lib32GoblinsRepo = [artixName: 'lib32-goblins', archName: 'multilib-staging', arch: 'multilib-staging-x86_64']

    RepoPackage(Script script) {
        this.script = script
    }

    def getPkgTrunk() {
        pkgTrunk
    }
    def setPkgTrunk(value) {
        pkgTrunk = value
    }

    def getPkgRepo() {
        pkgRepo
    }
    def setPkgRepo(value) {
        pkgRepo = value
    }

    def getAddArgs() {
        addArgs
    }
    def setAddArgs(value) {
        addArgs = value
    }

    def getRmArgs() {
        rmArgs
    }
    def setRmArgs(value) {
        rmArgs = value
    }

    def getBuildArgs() {
        buildArgs
    }
    def setBuildArgs(value) {
        buildArgs = value
    }

    def getIsAdd() {
        isAdd
    }
    def setIsAdd(value) {
        isAdd = value
    }

    def getIsRemove() {
        isRemove
    }
    def setIsRemove(value) {
        isRemove = value
    }

    def getIsBuild() {
        isBuild
    }
    def setIsBuild(value) {
        isBuild = value
    }

    def getIsBuildSuccess() {
        isBuildSuccess
    }
    def setIsBuildSuccess(value) {
        isBuildSuccess = value
    }

    void postBuild() {
        isBuildSuccess = true
        addArgs.add('-s')
    }

    private String getRepo(String src) {
        String repo = ''
        if ( src == goblinsRepo.arch || src == goblinsRepo.any ) {
            repo = goblinsRepo.artixName
        } else if ( src == gremlinsRepo.arch || src == gremlinsRepo.any ) {
            repo = gremlinsRepo.artixName
        } else if ( src == systemRepo.arch || src == systemRepo.any ) {
            repo = systemRepo.artixName
        } else if ( src == worldRepo.arch || src == worldRepo.any ) {
            repo = worldRepo.artixName
        } else if ( src == galaxyGoblinsRepo.arch || src == galaxyGoblinsRepo.any ) {
            repo = galaxyGoblinsRepo.artixName
        } else if ( src == galaxyGremlinsRepo.arch || src == galaxyGremlinsRepo.any ) {
            repo = galaxyGremlinsRepo.artixName
        } else if ( src == galaxyRepo.arch || src == galaxyRepo.any ) {
            repo = galaxyRepo.artixName
        } else if ( src == lib32GoblinsRepo.arch ) {
            repo = lib32GoblinsRepo.artixName
        } else if ( src == lib32GremlinsRepo.arch ) {
            repo = lib32GremlinsRepo.artixName
        } else if ( src == lib32Repo.arch ) {
            repo = lib32Repo.artixName
        }
        return repo
    }

    private List<String> getRepos(String src, String dest) {
        List<String> repoList = []
        if ( src == goblinsRepo.arch && dest == gremlinsRepo.arch ) {
            repoList.add(gremlinsRepo.artixName)
            repoList.add(goblinsRepo.artixName)
        } else if ( src == goblinsRepo.any && dest == gremlinsRepo.any ) {
            repoList.add(gremlinsRepo.artixName)
            repoList.add(goblinsRepo.artixName)
        } else if ( src == gremlinsRepo.arch && dest == goblinsRepo.arch ) {
            repoList.add(goblinsRepo.artixName)
            repoList.add(gremlinsRepo.artixName)
        } else if ( src == gremlinsRepo.any && dest == goblinsRepo.any ) {
            repoList.add(goblinsRepo.artixName)
            repoList.add(gremlinsRepo.artixName)
        } else if ( src.contains(systemRepo.archName) && dest == gremlinsRepo.arch ) {
            repoList.add(gremlinsRepo.artixName)
            repoList.add(systemRepo.artixName)
        } else if ( src.contains(systemRepo.archName) && dest == gremlinsRepo.any ) {
            repoList.add(gremlinsRepo.artixName)
            repoList.add(systemRepo.artixName)
        } else if ( src == gremlinsRepo.arch && dest.contains(systemRepo.archName) ) {
            repoList.add(systemRepo.artixName)
            repoList.add(gremlinsRepo.artixName)
        } else if ( src == gremlinsRepo.any && dest.contains(systemRepo.archName) ) {
            repoList.add(systemRepo.artixName)
            repoList.add(gremlinsRepo.artixName)
        } else if ( src.contains(worldRepo.archName) && dest == gremlinsRepo.arch ) {
            repoList.add(gremlinsRepo.artixName)
            repoList.add(worldRepo.artixName)
        } else if ( src.contains(worldRepo.archName) && dest == gremlinsRepo.any ) {
            repoList.add(gremlinsRepo.artixName)
            repoList.add(worldRepo.artixName)
        } else if ( src == gremlinsRepo.arch && dest.contains(worldRepo.archName) ) {
            repoList.add(worldRepo.artixName)
            repoList.add(gremlinsRepo.artixName)
        } else if ( src == gremlinsRepo.any && dest.contains(worldRepo.archName) ) {
            repoList.add(worldRepo.artixName)
            repoList.add(gremlinsRepo.artixName)
        } else if ( src.contains(systemRepo.archName) && dest.contains(worldRepo.archName) ) {
            repoList.add(worldRepo.artixName)
            repoList.add(systemRepo.artixName)
        } else if ( src.contains(worldRepo.archName) && dest.contains(systemRepo.archName) ) {
            repoList.add(systemRepo.artixName)
            repoList.add(worldRepo.artixName)
        } else if ( src == galaxyRepo.arch && dest.contains(worldRepo.archName) ) {
            repoList.add(worldRepo.artixName)
            repoList.add(galaxyRepo.artixName)
        } else if ( src == galaxyRepo.any && dest.contains(worldRepo.archName) ) {
            repoList.add(worldRepo.artixName)
            repoList.add(galaxyRepo.artixName)
        } else if ( src.contains(worldRepo.archName) && dest == galaxyRepo.arch ) {
            repoList.add(galaxyRepo.artixName)
            repoList.add(worldRepo.artixName)
        } else if ( src.contains(worldRepo.archName) && dest == galaxyRepo.any ) {
            repoList.add(galaxyRepo.artixName)
            repoList.add(worldRepo.artixName)
        } else if ( src == galaxyRepo.arch && dest.contains(systemRepo.archName) ) {
            repoList.add(systemRepo.artixName)
            repoList.add(galaxyRepo.artixName)
        } else if ( src == galaxyRepo.any && dest.contains(systemRepo.archName) ) {
            repoList.add(systemRepo.artixName)
            repoList.add(galaxyRepo.artixName)
        } else if ( src.contains(systemRepo.archName) && dest == galaxyRepo.arch ) {
            repoList.add(galaxyRepo.artixName)
            repoList.add(systemRepo.artixName)
        } else if ( src.contains(systemRepo.archName) && dest == galaxyRepo.any ) {
            repoList.add(galaxyRepo.artixName)
            repoList.add(systemRepo.artixName)
        } else if ( src.contains(galaxyGoblinsRepo.archName) && dest.contains(galaxyGremlinsRepo.archName) ) {
            repoList.add(galaxyGremlinsRepo.artixName)
            repoList.add(galaxyGoblinsRepo.artixName)
        } else if ( src.contains(galaxyGremlinsRepo.archName) && dest.contains(galaxyGoblinsRepo.archName) ) {
            repoList.add(galaxyGoblinsRepo.artixName)
            repoList.add(galaxyGremlinsRepo.artixName)
        } else if ( src.contains(galaxyGremlinsRepo.archName) && dest == galaxyRepo.arch ) {
            repoList.add(galaxyRepo.artixName)
            repoList.add(galaxyGremlinsRepo.artixName)
        } else if ( src.contains(galaxyGremlinsRepo.archName) && dest == galaxyRepo.any ) {
            repoList.add(galaxyRepo.artixName)
            repoList.add(galaxyGremlinsRepo.artixName)
        } else if ( src == galaxyRepo.arch && dest.contains(galaxyGremlinsRepo.archName) ) {
            repoList.add(galaxyGremlinsRepo.artixName)
            repoList.add(galaxyRepo.artixName)
        } else if ( src == galaxyRepo.any && dest.contains(galaxyGremlinsRepo.archName) ) {
            repoList.add(galaxyGremlinsRepo.artixName)
            repoList.add(galaxyRepo.artixName)
        } else if ( src.contains(lib32GoblinsRepo.archName) && dest.contains(lib32GremlinsRepo.archName) ) {
            repoList.add(lib32GremlinsRepo.artixName)
            repoList.add(lib32GoblinsRepo.artixName)
        } else if ( src.contains(lib32GremlinsRepo.archName) && dest.contains(lib32GoblinsRepo.archName) ) {
            repoList.add(lib32GoblinsRepo.artixName)
            repoList.add(lib32GremlinsRepo.artixName)
        } else if ( src.contains(lib32GremlinsRepo.archName) && dest.contains(lib32Repo.arch) ) {
            repoList.add(lib32Repo.artixName)
            repoList.add(lib32GremlinsRepo.artixName)
        } else if ( src.contains(lib32Repo.arch) && dest.contains(lib32GremlinsRepo.archName) ) {
            repoList.add(lib32GremlinsRepo.artixName)
            repoList.add(lib32Repo.artixName)
        } else if ( src.contains(galaxyGremlinsRepo.archName) && dest == gremlinsRepo.arch ) {
            repoList.add(gremlinsRepo.artixName)
            repoList.add(galaxyGremlinsRepo.artixName)
        } else if ( src.contains(galaxyGremlinsRepo.archName) && dest == gremlinsRepo.any ) {
            repoList.add(gremlinsRepo.artixName)
            repoList.add(galaxyGremlinsRepo.artixName)
        } else if ( src  == gremlinsRepo.arch && dest.contains(galaxyGremlinsRepo.archName) ) {
            repoList.add(galaxyGremlinsRepo.artixName)
            repoList.add(gremlinsRepo.artixName)
        } else if ( src  == gremlinsRepo.any && dest.contains(galaxyGremlinsRepo.archName) ) {
            repoList.add(galaxyGremlinsRepo.artixName)
            repoList.add(gremlinsRepo.artixName)
        } else if ( src.contains(galaxyGoblinsRepo.archName) && dest == goblinsRepo.arch ) {
            repoList.add(goblinsRepo.artixName)
            repoList.add(galaxyGoblinsRepo.artixName)
        } else if ( src.contains(galaxyGoblinsRepo.archName) && dest == goblinsRepo.any ) {
            repoList.add(goblinsRepo.artixName)
            repoList.add(galaxyGoblinsRepo.artixName)
        } else if ( src  == goblinsRepo.arch && dest.contains(galaxyGoblinsRepo.archName) ) {
            repoList.add(galaxyGoblinsRepo.artixName)
            repoList.add(goblinsRepo.artixName)
        } else if ( src  == goblinsRepo.any && dest.contains(galaxyGoblinsRepo.archName) ) {
            repoList.add(galaxyGoblinsRepo.artixName)
            repoList.add(goblinsRepo.artixName)
        }
        return repoList
    }

    void configureRepo(List<String> changeset) {
        List<String> pkgPathState = []

        for ( int i = 0; i < changeset.size(); i++ ) {
            List<String> entry = changeset[i].split()
            String fileStatus = entry[0]
            for ( int j = 1; j < entry.size(); j++ ) {
                if ( entry[j].contains('/PKGBUILD') && entry[j].contains('repos/') ){
                    pkgPathState.add("${fileStatus} " + entry[j].minus('/PKGBUILD'))
                }
            }
        }
        script.echo "pkgPathState: ${pkgPathState}"

        byte pkgCount = pkgPathState.size()
        script.echo "pkgCount: ${pkgCount}"

        if ( pkgCount > 0 ) {
            List<String> pkgPath = []
            List<String> pkgState = []
            String srcRepo = ''
            String destRepo = ''
            String repoAdd = ''
            String repoRemove = ''
            String srcPath = ''
            String destPath = ''
            String srcState = ''
            String destState = ''

            if ( pkgCount == 1 ) {
                srcPath = pkgPathState[0].split()[1]
                pkgPath.add(srcPath)
                srcState = pkgPathState[0].split()[0]
                pkgState.add(srcState)

                if ( pkgPath[0].tokenize('/').size() == 3 ) {
                    srcRepo = pkgPath[0].tokenize('/')[2]
                    pkgTrunk = pkgPath[0].tokenize('/')[0] + '/trunk'
                } else {
                    srcRepo = pkgPath[0].tokenize('/')[1]
                    pkgTrunk = 'trunk'
                }

                if ( pkgState[0] == 'A' || pkgState[0] == 'M' ) {
                    isBuild = true
                    repoAdd = getRepo(srcRepo)
                    buildArgs.add(repoAdd)
                } else if ( pkgState[0] == 'D' ) {
                    isRemove = true
                    repoRemove = getRepo(srcRepo)
                }
                pkgRepo = pkgPath[0]
            } else if ( pkgCount == 2 ) {
                srcPath = pkgPathState[0].split()[1]
                destPath = pkgPathState[1].split()[1]

                pkgPath.add(srcPath)
                pkgPath.add(destPath)

                srcState = pkgPathState[0].split()[0]
                destState = pkgPathState[1].split()[0]

                pkgState.add(srcState)
                pkgState.add(destState)

                if ( pkgPath[0].tokenize('/').size() == 3 ) {
                    srcRepo = pkgPath[0].tokenize('/')[2]
                    pkgTrunk = pkgPath[0].tokenize('/')[0] + '/trunk'
                } else {
                    srcRepo = pkgPath[0].tokenize('/')[1]
                    pkgTrunk = 'trunk'
                }

                if ( pkgPath[1].tokenize('/').size() == 3 ) {
                    destRepo = pkgPath[1].tokenize('/')[2]
                } else {
                    destRepo = pkgPath[1].tokenize('/')[1]
                }

                if ( pkgState[0] == 'M' ) {
                    isAdd = true
                    repoAdd = getRepo(srcRepo)
                    pkgRepo = pkgPath[1]
                } else if ( pkgState[1] == 'M' ) {
                    isAdd = true
                    repoAdd = getRepo(destRepo)
                    pkgRepo = pkgPath[0]
                }

                if ( pkgState[0] == 'D' ) {
                    isRemove = true
                    repoRemove = getRepo(srcRepo)
                    pkgRepo = pkgPath[1]
                } else if ( pkgState[1] == 'D' ) {
                    isRemove = true
                    repoRemove = getRepo(destRepo)
                    pkgRepo = pkgPath[0]
                }

                if ( pkgState[0].contains('R') && pkgState[1].contains('R') )  {
                    isAdd = true
                    isRemove = true
                    repoAdd = getRepos(srcRepo, destRepo)[0]
                    repoRemove = getRepos(srcRepo, destRepo)[1]
                    pkgRepo = pkgPath[1]
                }
            }
            addArgs.add(repoAdd)
            rmArgs.add(repoRemove)
            script.echo "pkgRepo: ${pkgRepo}"
        }
    }
}
