import java.util.Properties // Import required to read the local.properties file

plugins {
    alias(libs.plugins.android.application)
}

// --- SECURE KEY LOADING: Read API Key from local.properties ---
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
// Get the key from the file; use empty string if the key is missing
val apiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""

android {
    namespace = "com.deakin.task61learningassistant"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.deakin.task61learningassistant"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inject the API key into your app's BuildConfig class
        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
    }

    // Enable BuildConfig generation to access the key in Kotlin code
    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Android UI and Core libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // OkHttp library for making API calls to Gemini
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}