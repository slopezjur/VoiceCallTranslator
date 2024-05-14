plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.gms.google.services)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.plugin.serialization)

    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.sergiolopez.voicecalltranslator"
    compileSdk = 34

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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }

    defaultConfig {
        applicationId = "com.sergiolopez.voicecalltranslator"
        minSdk = 28
        resourceConfigurations += listOf("en", "es") // All supported languages in your app
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    val composeBom = platform(libs.compose.bom)
    val firebaseBom = platform(libs.firebase.bom)
    val openAiBom = platform(libs.openai.client.bom)

    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(composeBom)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material.iconsext)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.datastore.preferences)
    implementation(firebaseBom)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.navigation.compose)
    implementation(openAiBom)
    implementation(libs.openai.client)
    //implementation(libs.web.rtc)

    runtimeOnly(libs.ktor.client.okhttp)

    kapt(libs.hilt.android.compiler)

    // Tests
    //testImplementation(libs.junit)
    testImplementation(libs.androidx.test.rules)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.android)

    // Instrumentation
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}