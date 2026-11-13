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
    // Windows: DPAPI keeps the remembered account encrypted for the current user (owner decision D2).
    implementation(libs.jna.platform)
    testImplementation(kotlin("test"))
    testImplementation(compose.desktop.uiTestJUnit4)
    testImplementation(libs.ktor.client.mock)
}

compose.desktop {
    application {
        mainClass = "io.github.mrsimkin.dndcustomaid.desktop.MainKt"

        // Owner QA identity of the Windows app; matches the Android candidate 0.6.0-preqa.2 (build 60200).
        nativeDistributions {
            packageName = "DnD Custom Aid"
            packageVersion = "0.6.2"
            description = "D&D Custom Aid - App de PC y Escritorio DM"
            // The bundled Java runtime is trimmed; SQLite (java.sql), Ktor and Compose need these.
            // jdk.crypto.ec: TLS with the hosted API; java.naming: name lookups used by the HTTP stack.
            modules("java.instrument", "java.management", "java.sql", "jdk.unsupported", "jdk.crypto.ec", "java.naming")
        }
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
