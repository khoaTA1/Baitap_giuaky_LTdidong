plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.bt1"
    compileSdk = 36

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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    
    // Retrofit - Thư viện để gọi API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson Converter - Tự động chuyển đổi JSON từ API thành đối tượng Java
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp - HTTP client cho Retrofit
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Glide - Thư viện để tải và hiển thị ảnh từ URL một cách hiệu quả
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    
    // ViewModel và LiveData - MVVM Architecture Components
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")
    annotationProcessor("androidx.lifecycle:lifecycle-compiler:2.7.0")
    
    // Room Database - Local database (optional, để lưu cache offline)
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    
    // RecyclerView - Hiển thị danh sách
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    // CardView - Material CardView
    implementation("androidx.cardview:cardview:1.0.0")
    
    // SwipeRefreshLayout - Pull to refresh
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    
    // Gson - JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")
    
    // CircleImageView - Ảnh tròn cho avatar
    implementation("de.hdodenhof:circleimageview:3.1.0")
}