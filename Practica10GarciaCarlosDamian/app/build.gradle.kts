plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Aplicar el plugin de Google Services
    id("com.google.gms.google-services")
}

android {
    namespace = "garcia.carlosdamian.practica10_garciacarlosdamian"
    compileSdk = 35

    defaultConfig {
        applicationId = "garcia.carlosdamian.practica10_garciacarlosdamian"
        minSdk = 24
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    // Firebase - Asegúrate de que la BoM (Bill of Materials) esté aquí
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    // Dependencia para la autenticación de Firebase
    implementation("com.google.firebase:firebase-auth")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}