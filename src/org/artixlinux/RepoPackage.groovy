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
                    repoAdd = script.helper.getRepo(srcRepo)
                    buildArgs.add(repoAdd)
                } else if ( pkgState[0] == 'D' ) {
                    isRemove = true
                    repoRemove = script.helper.getRepo(srcRepo)
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
                    repoAdd = script.helper.getRepo(srcRepo)
                    pkgRepo = pkgPath[1]
                } else if ( pkgState[1] == 'M' ) {
                    isAdd = true
                    repoAdd = script.helper.getRepo(destRepo)
                    pkgRepo = pkgPath[0]
                }

                if ( pkgState[0] == 'D' ) {
                    isRemove = true
                    repoRemove = script.helper.getRepo(srcRepo)
                    pkgRepo = pkgPath[1]
                } else if ( pkgState[1] == 'D' ) {
                    isRemove = true
                    repoRemove = script.helper.getRepo(destRepo)
                    pkgRepo = pkgPath[0]
                }

                if ( pkgState[0].contains('R') && pkgState[1].contains('R') )  {
                    isAdd = true
                    isRemove = true
                    repoAdd = script.helper.getRepos(srcRepo, destRepo)[0]
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
}
