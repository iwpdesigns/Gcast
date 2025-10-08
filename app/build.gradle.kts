plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

// CI / secure signing support:
// The build will look for signing configuration from environment variables or project properties.
// Recommended CI approach: store your keystore as a GitHub Secret (base64 encoded), decode it in the workflow,
// then set ANDROID_KEYSTORE_PATH and the password variables for Gradle to pick up.
val signingStoreFilePath: String? = System.getenv("ANDROID_KEYSTORE_PATH") ?: project.findProperty("SIGNING_STORE_FILE")?.toString()
val signingStorePassword: String? = System.getenv("ANDROID_KEYSTORE_PASSWORD") ?: project.findProperty("SIGNING_STORE_PASSWORD")?.toString()
val signingKeyAlias: String? = System.getenv("ANDROID_KEY_ALIAS") ?: project.findProperty("SIGNING_KEY_ALIAS")?.toString()
val signingKeyPassword: String? = System.getenv("ANDROID_KEY_PASSWORD") ?: project.findProperty("SIGNING_KEY_PASSWORD")?.toString()

android {
    namespace = "com.gcast"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gcast"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // Configure signing if environment or properties are present
        if (signingStoreFilePath != null) {
            create("releaseConfig") {
                storeFile = file(signingStoreFilePath)
                storePassword = signingStorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (signingStoreFilePath != null) {
                signingConfig = signingConfigs.getByName("releaseConfig")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    
    lint {
        // Re-enabled lint for analysis
        // Remove specific disables to see lint issues
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.3")
    implementation("androidx.compose.ui:ui:1.6.3")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.3")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("com.google.android.gms:play-services-cast-framework:21.5.0")
    implementation("com.google.android.material:material:1.11.0")
    
    // Keep existing dependencies for compatibility
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.1.1")
    testImplementation("org.mockito:mockito-inline:5.1.1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    
    // Debug dependencies
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.3")
}

// Re-enabled lint tasks to identify and fix issues
// Removed the afterEvaluate block that disabled all lint tasks