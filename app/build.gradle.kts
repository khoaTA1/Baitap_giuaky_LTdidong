plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.bt1"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.bt1"
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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Retrofit - Thư viện để gọi API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson Converter - Tự động chuyển đổi JSON từ API thành đối tượng Java
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Glide - Thư viện để tải và hiển thị ảnh từ URL một cách hiệu quả
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:34.6.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("androidx.lifecycle:lifecycle-process:2.6.2")
}