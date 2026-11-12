plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

val ciDebugKeystore = file("ci-debug.keystore")

android {
    namespace = "io.github.mrsimkin.dndcustomaid.android"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.mrsimkin.dndcustomaid"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 60100
        versionName = "0.6.0-preqa.1"
    }

    if (ciDebugKeystore.exists()) {
        signingConfigs {
            create("ciDebug") {
                storeFile = ciDebugKeystore
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    buildTypes {
        getByName("debug") {
            if (ciDebugKeystore.exists()) {
                signingConfig = signingConfigs.getByName("ciDebug")
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets.getByName("main").assets.srcDir(rootProject.file("assets"))
    // Base para DM: the desks shared with the Windows app, compiled here with this App's own Compose.
    sourceSets.getByName("main").kotlin.srcDir(rootProject.file("dmUi/src/kotlin"))
}

dependencies {
    implementation(projects.shared)
    implementation(libs.descope.kotlin)
    implementation(libs.pdfbox.android)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation("androidx.compose.animation:animation")
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
