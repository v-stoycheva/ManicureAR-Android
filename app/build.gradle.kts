plugins {
    alias(libs.plugins.android.application)
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
}

android {
    namespace = "com.viktoriastoycheva.manicurear"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.viktoriastoycheva.manicurear"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("io.github.sceneview:arsceneview:0.10.0")
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("com.google.mediapipe:tasks-vision:0.10.14")
    ksp("androidx.room:room-compiler:$room_version")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    val cameraxVersion = "1.3.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // Retrofit за мрежови заявки
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson конвертор за превръщане на JSON в Java обекти
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp за логване (помага при грешки)
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}
