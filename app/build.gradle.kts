plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.taskiapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.taskiapp"
        minSdk = 33
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
            isMinifyEnabled = false // Cambia a true si usas ProGuard
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro" // Asegúrate de configurar este archivo
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
        kotlinCompilerExtensionVersion = "1.5.1" // Asegúrate de que esta versión sea compatible con tu versión de Compose
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx) // Biblioteca de soporte de Android
    implementation(libs.androidx.lifecycle.runtime.ktx) // Para usar LiveData
    implementation(libs.androidx.activity.compose) // Activity para Compose
    implementation(platform(libs.androidx.compose.bom)) // Bill of Materials para Compose
    implementation(libs.androidx.material3) // Material 3
    implementation(libs.firebase.auth.ktx) // Firebase Authentication
    implementation(libs.firebase.firestore.ktx) // Firestore
    implementation(libs.androidx.ui.test.android) // Para pruebas
    testImplementation(libs.junit) // Para pruebas unitarias
    androidTestImplementation(libs.androidx.junit) // JUnit para pruebas de Android
    androidTestImplementation(libs.androidx.espresso.core) // Espresso para pruebas de UI
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Bill of Materials para Compose en pruebas
    androidTestImplementation(libs.androidx.ui.test.junit4) // UI Testing para Compose
    debugImplementation(libs.androidx.ui.tooling) // Herramientas de diseño para Compose
    debugImplementation(libs.androidx.ui.test.manifest) // Manifest para pruebas de UI
}

// Aplica el plugin de Google Services al final
apply(plugin = "com.google.gms.google-services")
