plugins {
    alias(libs.plugins.android.application)
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
    }

    buildTypes {
        debug {
            buildConfigField "String", "API_URL", "\"http://192.168.1.100:8080/api/\""
            buildConfigField "int", "API_TIMEOUT", "30"
        }
        release {
            buildConfigField "String", "API_URL", "\"https://api.yourdomain.com/api/\""
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    dependencies {
    // Retrofit để gọi API
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0' // Để chuyển JSON sang Object
}
}