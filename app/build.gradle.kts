plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.salvatucomida"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.salvatucomida"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
    // Para las animaciones Lottie
    implementation("com.airbnb.android:lottie:6.1.0")
// Para las notificaciones programadas
    implementation("androidx.work:work-runtime:2.8.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}