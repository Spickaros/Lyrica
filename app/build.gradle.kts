plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.lyrica"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.lyrica"
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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }

    buildFeatures {
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation(files("libs/acrcloud-universal-sdk-1.3.26.jar"))
    implementation(files("src/main/jniLibs/arm64-v8a/libACRCloudUniversalEngine.so"))
    implementation(files("src/main/jniLibs/armeabi-v7a/libACRCloudUniversalEngine.so"))
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.compose.material:material:1.7.2")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.ui:ui:compose_ui_version")
    implementation("androidx.compose.ui:ui-tooling-preview:compose_ui_version")
    implementation("io.ktor:ktor-client-android:2.2.4")
    implementation("com.aallam.openai:openai-client:3.0.0")
    implementation("com.google.dagger:hilt-android:2.45")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.compose.ui:ui-tooling:1.7.2")
    implementation("androidx.compose.ui:ui-tooling-preview:1.7.2")
    implementation("com.github.bumptech.glide:glide:4.15.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("androidx.compose.material3:material3-android:1.3.0")
    implementation("com.google.firebase:firebase-messaging:24.0.2")
    annotationProcessor("com.github.bumptech.glide:compiler:4.14.2")

    // osmdroid and Google Maps
    implementation("org.osmdroid:osmdroid-android:6.1.14")
    implementation("com.google.android.gms:play-services-maps:19.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.2")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")
    implementation(composeBom)

    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation ("com.google.firebase:firebase-messaging")

}
