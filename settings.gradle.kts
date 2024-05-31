pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        gradlePluginPortal()
        google()
        maven {url=uri("https://jitpack.io")}
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {url=uri("https://jitpack.io")}
       /* maven {
            url =uri("https://pkgs.dev.azure.com/devops-test/test/_packaging/test/maven/v1")
        }*/
    }

}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "publishing-android-library-on-azure-artifacts"
include(":app")
include(":Library")
