#!/usr/bin/env groovy

// org/artix/globals.groovy
package org.artix

class globals {
    String PKG_TRUNK = ''
    String PKG_PATH = ''
    String ADD_ARGS = ''
    String RM_ARGS = ''
    String BUILD_ARGS = ''
    Boolean IS_ADD = false
    Boolean IS_REMOVE = false
    Boolean IS_BUILD = false
    Boolean IS_BUILD_SUCCESS = false

    String getCommit() {
        return sh(returnStdout: true, script: 'git rev-parse @').trim()
    }

    List<String> getChangeSet(String commit) {
        return sh(returnStdout: true, script: "git show --pretty=format: --name-status ${commit}").tokenize('\n')
    }

    void configureRepo() {

        String currentCommit = getCommit()
        List<String> changeSet = getChangeSet(currentCommit)

        List<String> pkgPathState = getPathState(changeSet)

        echo "currentCommit: ${currentCommit}"
        echo "pkgPathState: ${pkgPathState}"

        byte pkgCount = pkgPathState.size()

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
                    IS_BUILD = true
                    repoAdd = getRepo(srcRepo)
                    BUILD_ARGS = "-r ${repoAdd}"
                } else if ( pkgState[0] == 'D' ) {
                    IS_REMOVE = true
                    repoRemove = getRepo(srcRepo)
                }
                PKG_PATH = pkgPath[0]
                PKG_TRUNK = pkgPath[0].tokenize('/')[0] + '/trunk'
            } else if ( pkgCount == 2 ) {
                pkgPath.add(pkgPathState[0].split()[1])
                pkgPath.add(pkgPathState[1].split()[1])
                pkgState.add(pkgPathState[0].split()[0])
                pkgState.add(pkgPathState[1].split()[0])
                srcRepo = pkgPath[0].tokenize('/')[2]
                destRepo = pkgPath[1].tokenize('/')[2]

                if ( pkgState[0] == 'M' ) {
                    IS_ADD = true
                    repoAdd = getRepo(srcRepo)
                    PKG_PATH = pkgPath[1]
                } else if ( pkgState[1] == 'M' ) {
                    IS_ADD = true
                    repoAdd = getRepo(destRepo)
                    PKG_PATH = pkgPath[0]
                }

                if ( pkgState[0] == 'D' ) {
                    IS_REMOVE = true
                    repoRemove = getRepo(srcRepo)
                    PKG_PATH = pkgPath[1]
                } else if ( pkgState[1] == 'D' ) {
                    IS_REMOVE = true
                    repoRemove = getRepo(destRepo)
                    PKG_PATH = pkgPath[0]
                }

                if ( pkgState[0].contains('R') && pkgState[1].contains('R') )  {
                    IS_ADD = true
                    IS_REMOVE = true
                    repoAdd = getRepos(srcRepo, destRepo)[0]
                    repoRemove = getRepos(srcRepo, destRepo)[1]
                    PKG_PATH = pkgPath[1]
                }
                PKG_TRUNK = pkgPath[0].tokenize('/')[0] + '/trunk'
            }
            ADD_ARGS = "-a -d ${repoAdd}"
            RM_ARGS = "-r -d ${repoRemove}"
            echo "PKG_PATH: ${PKG_PATH}"
        }
    }

    void useSign() {
        IS_BUILD_SUCCESS = true
        ADD_ARGS += ' -s'
    }

//     Boolean isBuild() {
//         return IS_BUILD
//     }
//
//     Boolean isAdd() {
//         return IS_ADD
//     }
//
//     Boolean isRemove() {
//         return IS_REMOVE
//     }
//
//     Boolean isBuildSuccess() {
//         return IS_BUILD_SUCCESS
//     }

}





