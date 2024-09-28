plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

kapt {
    correctErrorTypes = true
}

android {
    namespace = "com.rohnsha.medbuddyai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rohnsha.medbuddyai"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
        mlModelBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("com.google.guava:guava:32.1.3-android")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.0")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.0")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.3.0")
    implementation("com.google.firebase:firebase-firestore:25.1.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-database-ktx")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.core:core-splashscreen:1.1.0-rc01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.5")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.5")

    //cameraX
    val cameraxVersion = "1.3.4"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-video:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("com.google.mlkit:text-recognition:16.0.1")

    //viewmodel
    val arch_version = "2.8.5"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$arch_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$arch_version")

    // room db
    val roomVersion= "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // credential manager
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("androidx.credentials:credentials:1.2.2")

    // onboardings
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.compose.ui:ui:1.7.0")
    implementation("androidx.compose.material:material:1.7.0")
    implementation("androidx.activity:activity-compose:1.9.2")
}