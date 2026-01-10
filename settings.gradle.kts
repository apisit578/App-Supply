pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://plugins.gradle.org/m2/") }

        maven {
            url = uri( "https://asia-southeast1-maven.pkg.dev/earth-616-362409/maven-google")
            credentials {
                username = "_json_key_base64"
                password = providers.gradleProperty("artifactRegistryMavenSecret").get()
            }
        }
        maven {
            url = uri( "https://asia-southeast1-maven.pkg.dev/earth-616-362409/maven-gradle")
            credentials {
                username = "_json_key_base64"
                password = providers.gradleProperty("artifactRegistryMavenSecret").get()
            }
        }
        maven {
            url = uri( "https://asia-southeast1-maven.pkg.dev/earth-616-362409/maven-repo")
            credentials {
                username = "_json_key_base64"
                password = providers.gradleProperty("artifactRegistryMavenSecret").get()
            }
        }
        maven {
            url = uri( "https://asia-southeast1-maven.pkg.dev/earth-616-362409/maven-public")
            credentials {
                username = "_json_key_base64"
                password = providers.gradleProperty("artifactRegistryMavenSecret").get()
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }

        maven {
            url = uri( "https://asia-southeast1-maven.pkg.dev/earth-616-362409/maven-google")
            credentials {
                username = "_json_key_base64"
                password = providers.gradleProperty("artifactRegistryMavenSecret").get()
            }
        }
        maven {
            url = uri( "https://asia-southeast1-maven.pkg.dev/earth-616-362409/maven-gradle")
            credentials {
                username = "_json_key_base64"
                password = providers.gradleProperty("artifactRegistryMavenSecret").get()
            }
        }
        maven {
            url = uri( "https://asia-southeast1-maven.pkg.dev/earth-616-362409/maven-repo")
            credentials {
                username = "_json_key_base64"
                password = providers.gradleProperty("artifactRegistryMavenSecret").get()
            }
        }
        maven {
            url = uri( "https://asia-southeast1-maven.pkg.dev/earth-616-362409/maven-public")
            credentials {
                username = "_json_key_base64"
                password = providers.gradleProperty("artifactRegistryMavenSecret").get()
            }
        }
    }
}

rootProject.name = "sage"
include(":app")
