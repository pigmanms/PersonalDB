plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}


android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    namespace = "com.pigmanms.personaldb"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pigmanms.personaldb"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Gson – JSON 직렬화/역직렬화
    implementation("com.google.code.gson:gson:2.10.1")
}