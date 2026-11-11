plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(17)

    sourceSets {
        main {
            resources.srcDir(rootProject.file("assets"))
            // The SRD bundled with the App (Base para DM), shared with the Android assets.
            resources.srcDir(rootProject.file("androidApp/src/main/assets"))
            // Base para DM: the desks shared with the Android tablet.
            kotlin.srcDir(rootProject.file("dmUi/src/kotlin"))
        }
    }
}

dependencies {
    implementation(projects.shared)
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.multiplatform.material)
    implementation(compose.material3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.pdfbox)
    testImplementation(kotlin("test"))
    testImplementation(compose.desktop.uiTestJUnit4)
}

compose.desktop {
    application {
        mainClass = "io.github.mrsimkin.dndcustomaid.desktop.MainKt"
    }
}


tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    val pcSheetProofDir = layout.buildDirectory.dir("pc-sheet-proofs")
    outputs.dir(pcSheetProofDir)
    systemProperty(
        "pcSheetProofDir",
        pcSheetProofDir.get().asFile.absolutePath,
    )
    testLogging {
        events("failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}
