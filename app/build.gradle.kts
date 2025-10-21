plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.infralerta"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.infralerta"
        minSdk = 24
        targetSdk = 34
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("org.osmdroid:osmdroid-android: 6.1.20")
    implementation("org.osmdroid:osmdroid-wms: 2.28.0")
    implementation("org.osmdroid:osmdroid-mapsforge:6.1.20")
    implementation("org.osmdroid:osmdroid-geopackage:6.1.20")
}