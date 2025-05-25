plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.0.21"
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.visara"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.visara"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/v1\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/v1\"")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
        }
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.hilt.android)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$2.8.7")
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.android.compiler)

    // For media playback using ExoPlayer
    implementation(libs.androidx.media3.exoplayer)
    // For DASH playback support with ExoPlayer
    implementation(libs.androidx.media3.exoplayer.dash)
    // For HLS playback support with ExoPlayer
    implementation(libs.androidx.media3.exoplayer.hls)
    // For SmoothStreaming playback support with ExoPlayer
    implementation(libs.androidx.media3.exoplayer.smoothstreaming)
    // For RTSP playback support with ExoPlayer
    implementation(libs.androidx.media3.exoplayer.rtsp)
    // For ad insertion using the Interactive Media Ads SDK with ExoPlayer
    implementation(libs.androidx.media3.exoplayer.ima)
    // For loading data using the Cronet network stack
    implementation(libs.androidx.media3.datasource.cronet)
    // For loading data using the OkHttp network stack
    implementation(libs.androidx.media3.datasource.okhttp)
    // For loading data using librtmp
    implementation(libs.androidx.media3.datasource.rtmp)
    // For building media playback UIs using Compose
    implementation(libs.androidx.media3.media3.ui.compose)
    // For building media playback UIs using Views
    implementation(libs.androidx.media3.ui)
    // For building media playback UIs using Jetpack Compose
    implementation(libs.androidx.media3.media3.ui.compose)
    // For building media playback UIs for Android TV using the Jetpack Leanback library
    implementation(libs.androidx.media3.ui.leanback)
    // For exposing and controlling media sessions
    implementation(libs.androidx.media3.session)
    // For extracting data from media containers
    implementation(libs.androidx.media3.extractor)
    // For integrating with Cast
    implementation(libs.androidx.media3.cast)
    // For scheduling background operations using Jetpack Work's WorkManager with ExoPlayer
    implementation(libs.androidx.media3.exoplayer.workmanager)
    // For transforming media files
    implementation(libs.androidx.media3.transformer)
    // For applying effects on video frames
    implementation(libs.androidx.media3.effect)
    // For muxing media files
    implementation(libs.androidx.media3.muxer)
    // Utilities for testing media components (including ExoPlayer components)
    implementation(libs.androidx.media3.test.utils)
    // Utilities for testing media components (including ExoPlayer components) via Robolectric
    implementation(libs.androidx.media3.test.utils.robolectric)
    // Common functionality for reading and writing media containers
    implementation(libs.androidx.media3.container)
    // Common functionality for media database components
    implementation(libs.androidx.media3.database)
    // Common functionality for media decoders
    implementation(libs.androidx.media3.decoder)
    // Common functionality for loading data
    implementation(libs.androidx.media3.datasource)
    // Common functionality used across multiple media libraries
    implementation(libs.androidx.media3.common)
    // Common Kotlin-specific functionality
    implementation(libs.androidx.media3.common.ktx)


    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.gson)

    implementation(libs.androidx.security.crypto)

    implementation(libs.androidx.navigation.compose)
    androidTestImplementation(libs.androidx.navigation.testing)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.androidx.room.testing)
    implementation(libs.androidx.room.paging)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
