import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.dagger.hilt.android)
//    alias(libs.plugins.jetbrains.kotlin.kapt) //replace by devtools.ksp
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.secrets.gradle.plugin)
    alias(libs.plugins.google.artifactregistry)
    alias(libs.plugins.org.sonarqube)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlinx.kover)
}

apply(from = "../kover.gradle")
apply(from = "../android-sonarqube.gradle")

val keystorePropertiesFile = rootProject.file("play/keystore.properties")
val keystoreProperties =
    Properties().apply {
        load(FileInputStream(keystorePropertiesFile))
    }

android {
    namespace = "th.co.cjexpress.store.sage"
    compileSdk = 35

    defaultConfig {
        applicationId = "th.co.cjexpress.store.sage"
        minSdk = 23
        targetSdk = 35
        versionCode = 5
        versionName = "1.10.0" +
            if (System.getenv("CI_PIPELINE_ID") != null) {
                "-${System.getenv("CI_PIPELINE_ID")}"
            } else {
                ""
            }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("$rootDir/play/sage.jks")
            storePassword = keystoreProperties["releaseStorePassword"] as String
            keyAlias = keystoreProperties["releaseKeyAlias"] as String
            keyPassword = keystoreProperties["releaseKeyPassword"] as String
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    productFlavors {
        flavorDimensions += listOf("env")
        create("CJDEV") {
            dimension = "env"
            versionNameSuffix = "D"
        }
        create("CJQA") {
            dimension = "env"
            versionNameSuffix = "Q"
        }
        create("CJUAT") {
            dimension = "env"
            versionNameSuffix = "U"
        }
        create("CJPROD") {
            dimension = "env"
            versionNameSuffix = "P"
        }
        create("CJXPROD") {
            dimension = "env"
            versionNameSuffix = "X"
        }
    }

    secrets {
        propertiesFileName = "secrets.properties"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Encrypt sharePref
    implementation(libs.androidx.security.crypto)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.compose)
    implementation(libs.androidx.runtime.livedata)

//    kapt(libs.hilt.compiler) //replace by devtools.ksp
    ksp(libs.hilt.compiler)

    // OkHttp
    implementation(libs.okhttp)

    // Gson
    implementation(libs.gson)

    // OAuth2
    implementation(libs.oauth2library)

    // jwt
    implementation(libs.jwtdecode)

    // Firebase Firestore
    implementation(libs.firebase.firestore)

    // Firebase Crashlytics
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ktx)

    // Firebase Analytics
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.analytics.ktx)

    // timber
    implementation(libs.timber)

    implementation(libs.accompanist.flowlayout)

    // Chucker
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.release)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.hamcrest)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.turbine)
    testImplementation(libs.robolectric)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.ui.test.junit4)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.test.manifest)
    implementation(libs.kotlin.reflect)
}
