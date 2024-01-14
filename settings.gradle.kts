pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DocumentScanner"

include(":app", ":opencv")

// Add OpenCV 4.9.0 Android SDK,
// the release is available at:
// https://github.com/opencv/opencv/releases/tag/4.9.0.
project(":opencv").projectDir = File(rootProject.projectDir, "dependencies/opencv/sdk")
