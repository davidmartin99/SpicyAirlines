plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.spicyairlines.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.spicyairlines.app"
        minSdk = 26
        targetSdk = 35
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
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    testOptions {
        unitTests.all {
            it.useJUnit {
                includeCategories("your.package.TestCategory")
            }
            it.jvmArgs("-noverify", "-XX:TieredStopAtLevel=1")
        }
    }


}

dependencies {

    // Dependencias de Jetpack y AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")


    // Firebase - Se asegura que BOM esté antes que las demás dependencias
    implementation(platform(libs.firebase.bom)) // Firebase BOM
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.foundation.android) // Firebase Authentication

    // Dependencias de pruebas (Unit Testing)
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation("org.mockito:mockito-inline:4.11.0") // Permite mockear clases finales
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("net.bytebuddy:byte-buddy:1.14.0")

    // Dependencias de pruebas instrumentadas (UI y Android Tests)
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.1")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("org.mockito:mockito-android:4.11.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1") // Para intents

    // Debug (solo para herramientas de desarrollo y pruebas visuales)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// Asegurarse de aplicar el plugin de Google Services
apply(plugin = "com.google.gms.google-services")
