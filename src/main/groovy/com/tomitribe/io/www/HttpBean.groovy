package com.tomitribe.io.www

import groovy.json.JsonSlurper

import java.nio.charset.StandardCharsets
import java.util.logging.Level
import java.util.logging.Logger

class HttpBean {
    public static final String BASE_URL = System.getProperty(
            'io_root_url',
            System.getenv()['io_root_url'] ?: 'https://api.github.com'
    )
    private Logger logger = Logger.getLogger('tribeio')
    private String token = System.getProperty("io.github.token", System.getenv()['github_atoken'])

    String getUrlContentWithToken(String path) {
        path.toURL().getText(
            [requestProperties: [
                'Accept': 'application/vnd.github.3.raw', 
                'Authorization': "token $token"
            ]],
            StandardCharsets.UTF_8.name()
        )
    }

    def loadGithubResourceJson(String projectName, String release, String resourceName) {
        def url = "${BASE_URL}/repos/${ServiceGithub.DOCUMENTATION_ROOT}/$projectName/contents/$resourceName?ref=$release"
        new JsonSlurper().parseText(getUrlContentWithToken(url))
    }

    String loadGithubResourceHtml(String projectName, String release, String resourceName) {
        def url = "${BASE_URL}/repos/${ServiceGithub.DOCUMENTATION_ROOT}/$projectName/contents/$resourceName?ref=$release"
        try {
            url.toURL().getText(
                    [requestProperties: [
                        'Accept': 'application/vnd.github.3.html',
                        'Authorization': "token $token"
                    ]],
                    StandardCharsets.UTF_8.name()
            )
        } catch (FileNotFoundException ignore) {
            return ''
        }
    }

    String loadGithubResourceEncoded(String projectName, String release, String resourceName) {
        try {
            def json = loadGithubResourceJson(projectName, release, resourceName)
            return json.content as String
        } catch (FileNotFoundException ignore) {
            // no-op
        } catch (exception) {
            logger.log(Level.INFO, "Impossible to load resource [$projectName, $release, $resourceName]", exception)
        }
        return ''
    }

    String loadGithubResource(String projectName, String release, String resourceName) {
        def encoded = loadGithubResourceEncoded(projectName, release, resourceName)
        if (encoded) {
            return new String(encoded.decodeBase64(), StandardCharsets.UTF_8.name())
        }
        return ''
    }

    List<String> loadGithubResourceNames(String projectName, String release, String resourceDir) {
        try {
            def json = loadGithubResourceJson(projectName, release, resourceDir)
            return json.collect { it.name }
        } catch (FileNotFoundException ignore) {
            // no-op
        } catch (exception) {
            logger.log(Level.INFO, "Impossible to load resource [$projectName, $release, $resourceName]", exception)
        }
    }
}
