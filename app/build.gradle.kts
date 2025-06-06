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
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE.txt",
                "META-INF/DEPENDENCIES.txt",
                "META-INF/LICENSE",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/LICENSE-notice.md", // Aquí añadimos el archivo problemático
                "META-INF/LICENSE-notice",    // Aseguramos excluir cualquier variante
                "META-INF/LICENSE*",          // Excluimos cualquier archivo de licencia duplicado
                "META-INF/NOTICE*"
            )
        }
    }
    testOptions {
        unitTests.all {
            it.useJUnit() // Usar JUnit 4 para Unit Tests
            it.jvmArgs("-noverify", "-XX:TieredStopAtLevel=1")
        }


        // Configuración para pruebas de UI y de Integración
        managedDevices {
            devices {
                create<com.android.build.api.dsl.ManagedVirtualDevice>("mediumPhoneApi34") {
                    device = "Pixel 5" // Puedes cambiar esto por "Pixel 4" o tu preferido
                    apiLevel = 34       // Usar API 34 (la de tu emulador)
                    systemImageSource = "aosp"
                }
            }
        }
        animationsDisabled = true // Desactivar animaciones para pruebas de UI
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
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")


    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.foundation.android) // Firebase Authentication


    // Dependencias de pruebas (Unit Testing) con MockK y JUnit 4
    testImplementation("io.mockk:mockk:1.13.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("junit:junit:4.13.2") // JUnit 4
    testImplementation("androidx.arch.core:core-testing:2.1.0") // LiveData testing
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.8.22") // Necesario para MockK


    // Dependencias de pruebas instrumentadas (UI y Android Tests)
    androidTestImplementation("io.mockk:mockk-android:1.13.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")


    // Debug (solo para herramientas de desarrollo y pruebas visuales)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}


// Asegurarse de aplicar el plugin de Google Services
apply(plugin = "com.google.gms.google-services")


