#!/usr/bin/env groovy

class PackageGlobals implements Serializable {
    private String pkgTrunk = ''
    private String pkgRepo = ''
    private List<String>  addArgs = ['deploypkg', '-a', '-d']
    private List<String> rmArgs = ['deploypkg', '-r', '-d']
    private List<String>  buildArgs = ['buildpkg', '-r']
    private Boolean isAdd = false
    private Boolean isRemove = false
    private Boolean isBuild = false
    private Boolean isBuildSuccess = false

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

}
