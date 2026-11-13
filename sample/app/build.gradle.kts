plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val magistrateVersion: String = (project.findProperty("magistrateVersion") as String?) ?: "0.1.0"

android {
    namespace = "com.octetproof.magistrate.sample"
    compileSdk = 36  // OctetSDK's own compileSdk; its androidx.work 2.12 needs 35+
    defaultConfig {
        applicationId = "com.octetproof.magistrate"   // PUBLIC channel
        minSdk = 30                                    // Magistrate / OctetSDK minimum
        targetSdk = 34
        versionCode = 1
        versionName = magistrateVersion
    }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.14" }  // matches Kotlin 1.9.24
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin { jvmToolchain(17) }

dependencies {
    implementation("com.octetproof:magistrate:$magistrateVersion")   // pulls com.octetproof:sdk transitively

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2024.09.02"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
