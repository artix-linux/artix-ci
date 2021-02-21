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

    Map getConfig() {
        config
    }

    Map getInfo() {
        info
    }

    private String findRepo(String name) {
        return config.repos.each { it }.find { it.vcs == name }.name
    }

    private void configureOperation() {

        String srcRepo = repoListGit[0].path.tokenize('/')[1]

        if ( srcRepo == 'rebuild' ) {
            config.tools.cmdBuild += " -m"
        }

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

        config.tools.cmdBuild += " -d ${config.src.repoAddName}"
        config.tools.cmdSign += " ${fileArgs}"
        config.tools.cmdRepoAdd += " -d ${config.src.repoAddName} ${fileArgs}"
        config.tools.cmdRepoRemove += " -d ${config.src.repoRemoveName} ${fileArgs}"
    }

    private String getBuildMailSubject(String msg) {
        return "[${config.src.repoAddName}] ${msg}: ${info.pkgbase.name}"
    }

    private String getBuildMailBody(String msg, String url) {
        return """
        <p><strong>${msg}</strong></p>
        <p>Build: ${config.src.repoPath}</p>
        <p>Name:</p>
        <p>${info.pkgbase.name}</p>
        <p>author: ${config.author.name}</p>
        <p>email: ${config.author.email}</p>
        <p><a href=${url}>${url}</a></p>
        """
    }

    void sendBuildMail(String msg, String sendto, String url) {
        steps.emailext (
            mimeType: config.notify.mime,
            body: getBuildMailBody(msg, url),
            subject: getBuildMailSubject(msg),
            to: sendto,
            attachLog: false,
            attachmentsPattern: "${config.arch}**/*.log",
            compressLog: true
        )
    }

    private String getRepoMailSubject(String msg, String action) {
        return "[${action}] ${msg}: ${info.pkgbase.name}"
    }

    private String getRepoMailBody(String msg, String repo) {
        return """
        <p><strong>${msg}</strong></p>
        <p>Repo: ${repo}</p>
        <p>Packages:</p>
        <p>${info.files.join('\n')}</p>
        <p>author: ${config.author.name}</p>
        <p>email: ${config.author.email}</p>
        """
    }

    void sendRepoMail(String msg) {
        steps.emailext (
            mimeType: config.notify.mime,
            body: getRepoMailBody(msg, config.src.repoAddName),
            subject: getRepoMailSubject(msg, config.src.repoAddName),
            to: config.notify.repos,
            attachLog: false
        )
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
