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

    void configurePkgRepo(List<String> changeset) {
        List<String> pkgPathState = []

        for ( int i = 0; i < changeset.size(); i++ ) {
            List<String> entry = changeset[i].split()
            String fileStatus = entry[0]
            for ( int j = 1; j < entry.size(); j++ ) {
                if ( entry[j].contains('/PKGBUILD') && entry[j].contains('/repos') ){
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
            if ( pkgCount == 1 ) {
                pkgPath.add(pkgPathState[0].split()[1])
                pkgState.add(pkgPathState[0].split()[0])
                srcRepo = pkgPath[0].tokenize('/')[2]

                if ( pkgState[0] == 'A' || pkgState[0] == 'M' ) {
                    isBuild = true
                    repoAdd = getRepo(srcRepo)
                    buildArgs.add(repoAdd)
                } else if ( pkgState[0] == 'D' ) {
                    isRemove = true
                    repoRemove = getRepo(srcRepo)
                }
                pkgRepo = pkgPath[0]
                pkgTrunk = pkgPath[0].tokenize('/')[0] + '/trunk'
            } else if ( pkgCount == 2 ) {
                pkgPath.add(pkgPathState[0].split()[1])
                pkgPath.add(pkgPathState[1].split()[1])
                pkgState.add(pkgPathState[0].split()[0])
                pkgState.add(pkgPathState[1].split()[0])
                srcRepo = pkgPath[0].tokenize('/')[2]
                destRepo = pkgPath[1].tokenize('/')[2]

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
                pkgTrunk = pkgPath[0].tokenize('/')[0] + '/trunk'
            }
            addArgs.add(repoAdd)
            rmArgs.add(repoRemove)
            script.echo "pkgRepo: ${pkgRepo}"
        }
    }

    String getRepo(String src) {
        String repo = ''
        if ( src == 'staging-x86_64' || src == 'staging-any' ) {
            repo = 'goblins'
        } else if ( src == 'testing-x86_64' || src == 'testing-any' ) {
            repo = 'gremlins'
        } else if ( src.contains('core') ) {
            repo = 'system'
        } else if ( src.contains('extra') ) {
            repo = 'world'
        } else if ( src.contains('community-staging') ) {
            repo = 'galaxy-goblins'
        } else if ( src.contains('community-testing') ) {
            repo = 'galaxy-gremlins'
        } else if ( src == 'community-x86_64' || src == 'community-any' ) {
            repo = 'galaxy'
        } else if ( src.contains('multilib-staging') ) {
            repo = 'lib32-goblins'
        } else if ( src.contains('multilib-testing') ) {
            repo = 'lib32-gremlins'
        } else if ( src.contains('multilib-x86_64') ) {
            repo = 'lib32'
        }
        return repo
    }

    List<String> getRepos(String src, String dest) {
        List<String> repoList = []
        if ( src == 'staging-x86_64' && dest == 'testing-x86_64' ) {
            repoList.add('gremlins')
            repoList.add('goblins')
        } else if ( src == 'staging-any' && dest == 'testing-any' ) {
            repoList.add('gremlins')
            repoList.add('goblins')
        } else if ( src == 'testing-x86_64' && dest == 'staging-x86_64' ) {
            repoList.add('goblins')
            repoList.add('gremlins')
        } else if ( src == 'testing-any' && dest == 'staging-any' ) {
            repoList.add('goblins')
            repoList.add('gremlins')
        } else if ( src.contains('core') && dest == 'testing-x86_64' ) {
            repoList.add('gremlins')
            repoList.add('system')
        } else if ( src.contains('core') && dest == 'testing-any' ) {
            repoList.add('gremlins')
            repoList.add('system')
        } else if ( src == 'testing-x86_64' && dest.contains('core') ) {
            repoList.add('system')
            repoList.add('gremlins')
        } else if ( src == 'testing-any' && dest.contains('core') ) {
            repoList.add('system')
            repoList.add('gremlins')
        } else if ( src.contains('extra') && dest == 'testing-x86_64' ) {
            repoList.add('gremlins')
            repoList.add('world')
        } else if ( src.contains('extra') && dest == 'testing-any' ) {
            repoList.add('gremlins')
            repoList.add('world')
        } else if ( src == 'testing-x86_64' && dest.contains('extra') ) {
            repoList.add('world')
            repoList.add('gremlins')
        } else if ( src == 'testing-any' && dest.contains('extra') ) {
            repoList.add('world')
            repoList.add('gremlins')
        } else if ( src.contains('core') && dest.contains('extra') ) {
            repoList.add('world')
            repoList.add('system')
        } else if ( src.contains('extra') && dest.contains('core') ) {
            repoList.add('system')
            repoList.add('world')
        } else if ( src == 'community-x86_64' && dest.contains('extra') ) {
            repoList.add('world')
            repoList.add('galaxy')
        } else if ( src == 'community-any' && dest.contains('extra') ) {
            repoList.add('world')
            repoList.add('galaxy')
        } else if ( src.contains('extra') && dest == 'community-x86_64' ) {
            repoList.add('galaxy')
            repoList.add('world')
        } else if ( src.contains('extra') && dest == 'community-any' ) {
            repoList.add('galaxy')
            repoList.add('world')
        } else if ( src == 'community-x86_64' && dest.contains('core') ) {
            repoList.add('system')
            repoList.add('galaxy')
        } else if ( src == 'community-any' && dest.contains('core') ) {
            repoList.add('system')
            repoList.add('galaxy')
        } else if ( src.contains('core') && dest == 'community-x86_64' ) {
            repoList.add('galaxy')
            repoList.add('system')
        } else if ( src.contains('core') && dest == 'community-any' ) {
            repoList.add('galaxy')
            repoList.add('system')
        } else if ( src.contains('community-staging') && dest.contains('community-testing') ) {
            repoList.add('galaxy-gremlins')
            repoList.add('galaxy-goblins')
        } else if ( src.contains('community-testing') && dest.contains('community-staging') ) {
            repoList.add('galaxy-goblins')
            repoList.add('galaxy-gremlins')
        } else if ( src.contains('community-testing') && dest == 'community-x86_64' ) {
            repoList.add('galaxy')
            repoList.add('galaxy-gremlins')
        } else if ( src.contains('community-testing') && dest == 'community-any' ) {
            repoList.add('galaxy')
            repoList.add('galaxy-gremlins')
        } else if ( src == 'community-x86_64' && dest.contains('community-testing') ) {
            repoList.add('galaxy-gremlins')
            repoList.add('galaxy')
        } else if ( src == 'community-any' && dest.contains('community-testing') ) {
            repoList.add('galaxy-gremlins')
            repoList.add('galaxy')
        } else if ( src.contains('multilib-staging') && dest.contains('multilib-testing') ) {
            repoList.add('lib32-gremlins')
            repoList.add('lib32-goblins')
        } else if ( src.contains('multilib-testing') && dest.contains('multilib-staging') ) {
            repoList.add('lib32-goblins')
            repoList.add('lib32-gremlins')
        } else if ( src.contains('multilib-testing') && dest.contains('multilib-x86_64') ) {
            repoList.add('lib32')
            repoList.add('lib32-gremlins')
        } else if ( src.contains('multilib-x86_64') && dest.contains('multilib-testing') ) {
            repoList.add('lib32-gremlins')
            repoList.add('lib32')
        }
        return repoList
    }
}
