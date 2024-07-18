dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/OpenXingZhi/ceridwen-util")
            credentials(PasswordCredentials::class)
        }
    }
}

rootProject.name = "sip-library"