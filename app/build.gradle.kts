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
        // minSdk should be 33 because that makes app language switching much easier and possible
        // via system settings. That way don't have to override locale on app launch. Additionally
        // the Geocoder API this app uses no longer needs fallback code for older versions.
        minSdk = 33
        targetSdk = 35
        versionCode = 3
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField(
            "String",
            "NOMINATIM_USER_AGENT",
            "\"${properties.getProperty("NOMINATIM_USER_AGENT") ?: "weather app android"}\""
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

    // Maps SDK for rendering maps.
    implementation(libs.maplibre.compose)

    // Charting library for weather data
    implementation(libs.compose.charts)

    // Lottie animations
    implementation(libs.lottie.compose)
}
