pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
    plugins {
        id("com.android.application") version "8.5.2"
        id("org.jetbrains.kotlin.android") version "1.9.24"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // The public Magistrate Maven repo (the mvn-repo branch, served raw).
        maven { url = uri("https://raw.githubusercontent.com/octetproof/octet-magistrate-android/mvn-repo") }
    }
}
rootProject.name = "MagistrateSample"
include(":app")
