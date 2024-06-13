plugins {
    alias(libs.plugins.androidApplication)
    id("kotlin-android")
}

android {
    namespace = "com.plantix"
    compileSdk = 34
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.plantix"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        buildConfigField("String", "URL_SERVER_BACKEND", "\"https://e4aa-117-4-244-234.ngrok-free.app\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.android.volley:volley:1.2.1")

    // Java language implementation
    implementation("androidx.fragment:fragment:1.7.0")
    // Kotlin
    implementation("androidx.fragment:fragment-ktx:1.7.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")

    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")

    // markdown
    implementation("io.noties.markwon:core:4.0.1")

    // AWS
    implementation("com.amazonaws:aws-android-sdk-core:2.22.0")
    implementation("com.amazonaws:aws-android-sdk-s3:2.22.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // slide show
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.12.0") // For better network performance
    implementation("com.github.bumptech.glide:annotations:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation("com.squareup.picasso:picasso:2.71828")

    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.core:core:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
//    implementation("com.android.support:design:28.0.0")
}
