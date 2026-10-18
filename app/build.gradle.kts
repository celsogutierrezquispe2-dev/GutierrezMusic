plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose") }
android {
 namespace = "com.gutierrez.music"
 compileSdk = 35
 defaultConfig { applicationId = "com.gutierrez.music"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0" }
 buildFeatures { compose = true }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
 kotlinOptions { jvmTarget = "17" }
}
dependencies {
 implementation(platform("androidx.compose:compose-bom:2025.04.01"))
 implementation("androidx.activity:activity-compose:1.10.1")
 implementation("androidx.compose.ui:ui")
 implementation("androidx.compose.foundation:foundation")
 implementation("androidx.compose.material3:material3")
 implementation("androidx.core:core-ktx:1.16.0")
 implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
 implementation("androidx.datastore:datastore-preferences:1.1.4")
 implementation("androidx.documentfile:documentfile:1.0.1")
 implementation("androidx.media3:media3-exoplayer:1.6.1")
 implementation("androidx.media3:media3-session:1.6.1")
 implementation("com.google.guava:guava:33.3.1-android")
 implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
}
