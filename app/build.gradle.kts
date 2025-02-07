plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.hilt.android.gradle)
    alias(libs.plugins.jetbrainsKotlinSerialization)

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.clerodri.binnacle"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.clerodri.binnacle"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

}

dependencies {
  //  Dagger Hilt
    implementation(libs.dagger.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.play.services.location)
    ksp(libs.dagger.hilt.android.compiler)
    ksp(libs.dagger.hilt.compiler)
//    implementation(libs.maps)


    //  Retrofit
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.gson.converter)

    //Icons extended
    implementation(libs.androidx.material.icons.extended)

    //Splash
    implementation(libs.androidx.core.splashscreen)


    //Navitation compose
    implementation(libs.kotlinx.serialization.json.v180)
    implementation(libs.androidx.navigation.compose)

    // GOOGE MAPS
//    implementation(libs.play.services.maps)
//    implementation(libs.maps.ktx)
//    implementation(libs.maps.utils.ktx)
    implementation(libs.maps.compose)
    implementation(libs.maps.compose.utils)
    implementation(libs.maps.compose.widgets)

    implementation (libs.accompanist.permissions)

    //Data store
    implementation(libs.androidx.datastore.preferences)

    //Animaticon for navigaction

    implementation(libs.accompanist.navigation.animation)



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
}
secrets{
    propertiesFileName = "secrets.properties"
    defaultPropertiesFileName = "local.defaults.properties"
}