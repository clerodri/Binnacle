import org.apache.commons.logging.LogFactory.release
import java.util.Properties

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
        versionCode = 4
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        register("release") {
            storeFile = file(project.property("KEYSTORE_FILE") as String)
            storePassword = project.property("KEYSTORE_PASSWORD") as String
            keyAlias = project.property("KEY_ALIAS") as String
            keyPassword = project.property("KEY_PASSWORD") as String
        }
    }

    val localPropertiesSecret  = Properties()
    val localProperties = Properties()
    val localPropertiesFileSecret = rootProject.file("secret.properties")
    val localPropertiesFile = rootProject.file("local.properties")
    if( localPropertiesFileSecret.exists() && localPropertiesFileSecret.isFile){
        localPropertiesSecret.load(localPropertiesFileSecret.inputStream())
    }
    if (localPropertiesFile.exists() && localPropertiesFile.isFile) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL_PROD", localProperties.getProperty("BASE_URL_PROD"))
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
           // buildConfigField("String", "BASE_URL", "\"http://192.168.100.70:8080/\"")
            buildConfigField("String", "BASE_URL", localProperties.getProperty("BASE_URL"))
            buildConfigField("String","GMP_KEY", localPropertiesSecret.getProperty("GMP_KEY"))
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
    //ROOM
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)


  //  Dagger Hilt
    implementation(libs.dagger.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.play.services.location)
    implementation(libs.androidx.camera.view)
    ksp(libs.dagger.hilt.android.compiler)
    ksp(libs.dagger.hilt.compiler)
//    implementation(libs.maps)


    //  Retrofit
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.gson.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.kotlin)
    implementation(libs.logging.interceptor)

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

    //CameraX
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.compose)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.camera.extensions)

    implementation(libs.lottie.compose)
    implementation(libs.coil.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.core.ktx)

    // MAPS GOOGLE
    implementation("com.google.maps.android:maps-compose:1.0.0")
    implementation("com.google.android.gms:play-services-maps:18.0.2")

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