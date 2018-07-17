#!/usr/bin/env groovy

static String getRepo(String src) {
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

static List<String> getRepos(String src, String dest) {
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
