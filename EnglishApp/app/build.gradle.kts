import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.englishapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.englishapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // map api key từ local peroperies vào buildconfig
        val localProps = Properties()
        val localPropsFile = rootProject.file("local.properties")
        if (localPropsFile.exists()) {
            localPropsFile.inputStream().use { localProps.load(it) }
        }
        val openAiKey = (localProps.getProperty("OPENAI_API_KEY") ?: "").trim()
        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiKey\"")

        val geminiApiKey = (localProps.getProperty("GEMINI_API_KEY") ?: "").trim()
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")

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
    buildFeatures {
        viewBinding = true
        buildConfig = true // tự dinh ra buildconfig
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    implementation(libs.play.services.measurement.api)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    
    // Firebase BOM - Quản lý phiên bản Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-analytics")
    
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

// Thư viện Analytics (hoặc Firestore/Auth sau này bạn cần)
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    // Thêm dòng này vào file build.gradle.kts
    implementation("com.google.firebase:firebase-database")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
// gemini ai
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    // dùng để gửi request từ phía client
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    // dependency để kết nối fire store
    implementation ("com.google.firebase:firebase-firestore")
    // dùng để sử dụng biểu đồ
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
