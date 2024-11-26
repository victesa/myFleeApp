plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.daggerHilt)
}

android {
    namespace = "com.victorkirui.myfleeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.victorkirui.myfleeapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes.addAll(
                listOf(
                    "/META-INF/{AL2.0,LGPL2.1}",
                    "META-INF/gradle/incremental.annotation.processors"
                )
            )
        }
    }



}

dependencies {

    //Room dependencies
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation (libs.androidx.room.ktx)

    //Status bar dependency
    implementation(libs.android.status.bar)

    //ViewModel Dependency
    implementation(libs.android.viewModel)

    //Dagger-hilt dependencies
    implementation(libs.android.hilt)
    ksp(libs.android.hilt.compiler)
    implementation(libs.android.hilt.navigation)

    //SplashScreen dependency
    implementation (libs.androidx.core.splashscreen)

    //Coil dependency
    implementation(libs.coil.compose)

    //Datastore dependency
    implementation (libs.androidx.datastore.preferences)

    // Firebase dependencies
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // For mock Firestore
    testImplementation(libs.firebase.firestore.testing)

    // Mockito testing
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline) // Mockito for mocking
    testImplementation (libs.mockito.core.v451)
    testImplementation( libs.mockito.kotlin)

}