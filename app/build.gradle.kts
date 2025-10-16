plugins {
    // Usa SOLO los alias del Version Catalog si ya los tienes definidos
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.bionica.visor_prueba3"
    // Si no tienes instalado el SDK 36 en tu PC, usa 34 para evitar errores
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bionica.visor_prueba3"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Forma recomendada sin warnings (requiere Kotlin Gradle Plugin 1.9+)
    kotlin {
        jvmToolchain(11)
    }
}

dependencies {
    // --- Dependencias vía Version Catalog (si existen en tu libs.versions.toml) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.mediarouter)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    // OJO: quitar lo que no uses. Si no estás usando Compose, borra esta línea:
    // implementation(libs.androidx.compose.material)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- Evita duplicados ---
    // (Ya tienes constraintlayout por catálogo, no lo repitas manualmente)

    // --- Firebase BOM + KTX (Kotlin DSL usa comillas dobles) ---
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // --- Google Maps (si no usas el catálogo para maps, comenta el de arriba y deja esta) ---
    // implementation("com.google.android.gms:play-services-maps:19.2.0")

    // --- Coil (carga de imágenes) ---
    implementation("io.coil-kt:coil:2.7.0")

    // --- RecyclerView (si no lo tienes en el catálogo) ---
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
}
