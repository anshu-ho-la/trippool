plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Release signing comes from the environment (the GitHub workflow sets these from its secrets), never from a file in
// the repository. Without them a release build is left unsigned, and debug builds are not affected.
val signingKeystorePath: String? = providers.environmentVariable("ANDROID_KEYSTORE_PATH").orNull
val signingKeystorePassword: String? = providers.environmentVariable("ANDROID_KEYSTORE_PASSWORD").orNull
val signingKeyAlias: String? = providers.environmentVariable("ANDROID_KEY_ALIAS").orNull
val signingKeyPassword: String? = providers.environmentVariable("ANDROID_KEY_PASSWORD").orNull

// Android only installs an update whose version code is higher than the installed one, so the workflow passes the
// build number. Local builds keep 1.
val tripVersionCode: Int = providers.gradleProperty("tripVersionCode").orNull?.toIntOrNull() ?: 1
val tripVersionName: String = providers.gradleProperty("tripVersionName").orNull?.takeIf { it.isNotBlank() } ?: "1.0"

android {
    namespace = "com.trippool.app"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.trippool.app"
        minSdk = 29
        targetSdk = 37
        versionCode = tripVersionCode
        versionName = tripVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (signingKeystorePath != null && signingKeystorePassword != null && signingKeyAlias != null && signingKeyPassword != null) {
            create("release") {
                storeFile = file(signingKeystorePath)
                storePassword = signingKeystorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}