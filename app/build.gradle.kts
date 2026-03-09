plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.death.coffeeroulette"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.death.coffeeroulette"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // 카카오 앱 키 (카카오 개발자 콘솔에서 발급받은 키로 교체)
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = "YOUR_KAKAO_NATIVE_APP_KEY"
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"YOUR_KAKAO_NATIVE_APP_KEY\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.01.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.animation:animation")

    // Activity & Navigation
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Core
    implementation("androidx.core:core-ktx:1.12.0")

    // Kakao SDK
    implementation("com.kakao.sdk:v2-user:2.19.0")
    implementation("com.kakao.sdk:v2-talk:2.19.0")
    implementation("com.kakao.sdk:v2-friend:2.19.0")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
}
