plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")

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
        debug {
            // URL para el emulador (10.0.2.2 apunta a tu PC local)
            buildConfigField("String", "BASE_URL", "\"https://10.0.2.2:7293/api/\"")
        }
        release {
            // URL de producción (cuando subas tu API a un servidor real)
            buildConfigField("String", "BASE_URL", "\"https://tu-servidor.com/api/\"")
            optimization {
                enable = false
            }
        }
    }
    buildFeatures {
        buildConfig = true
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

    // Lottie y WorkManager
    implementation("com.airbnb.android:lottie:6.1.0")
    implementation("androidx.work:work-runtime:2.8.1")

    // Glide con integración OkHttp
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.16.0")

    // Retrofit // Ai de recetas
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")


    // Glide para cargar imágenes desde el servidor
    implementation("com.github.bumptech.glide:glide:4.15.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.15.0")

    // ...  dependencias actuales Fireabase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth")
}
