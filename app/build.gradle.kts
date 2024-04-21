plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.gms.google.services)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.android)

    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.sergiolopez.voicecalltranslator"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sergiolopez.voicecalltranslator"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    val composeBom = platform(libs.compose.bom)
    val firebaseBom = platform(libs.firebase.bom)

    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(composeBom)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material.iconsext)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(firebaseBom)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.core.telecom)
    implementation(libs.navigation.compose)
    implementation(libs.web.rtc)

    kapt(libs.hilt.android.compiler)

    // Tests
    testImplementation(libs.junit)

    // Instrumentation
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}