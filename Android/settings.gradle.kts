rootProject.name = "PersonalDB"        // 원하는 프로젝트명
include(":app")                     // <‑‑ 반드시 포함!

pluginManagement {
    repositories {
        google()            // Android Gradle Plugin (AGP)
        mavenCentral()      // Kotlin 플러그인 등
        gradlePluginPortal()// 기타 커뮤니티 플러그인
    }
    plugins {
        id("com.android.application") version "8.9.1"
        id("org.jetbrains.kotlin.android") version "1.9.23"
    }
}

// 라이브러리 의존성 저장소
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}