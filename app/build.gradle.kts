import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField(
            "String",
            "WEATHER_API_BASE_URL",
            "\"${properties.getProperty("WEATHER_API_BASE_URL")}\""
        )
        buildConfigField(
            "String",
            "GEOCODING_API_BASE_URL",
            "\"${properties.getProperty("GEOCODING_API_BASE_URL")}\""
        )
        // How many fuzzy matches to return from the geocoding API.
        // 5 is the maximum value supported by the API.
        buildConfigField(
            "String",
            "GEOCODING_API_LIMIT",
            "\"${properties.getProperty("GEOCODING_API_LIMIT")?.toInt() ?: 5}\""
        )
        buildConfigField(
            "String",
            "OPEN_WEATHER_MAP_API_KEY",
            "\"${properties.getProperty("OPEN_WEATHER_MAP_API_KEY") ?: ""}\""
        )

        buildConfigField(
            "String",
            "MAX_FAVORITE_LOCATIONS",
            "\"${properties.getProperty("MAX_FAVORITE_LOCATIONS")?.toInt() ?: 5}\""
        )
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
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

    // navigation
    implementation(libs.androidx.navigation.compose)

    // network
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // coroutines
    implementation(libs.kotlinx.coroutines.android)

    // viewmodel and lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // coil async images
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // extra icons
    implementation(libs.androidx.material.icons.extended)

    // permission handling
    implementation(libs.accompanist.permissions)

    // Google play services to use fused location provider
    implementation(libs.play.services.location)
    // To use coroutines with the play services for cleaner code
    implementation(libs.kotlinx.coroutines.play.services)

    // room database for storing locations data
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Datastore for storing preferences
    implementation(libs.androidx.datastore.preferences)
}
