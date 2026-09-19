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
        }
    }
}

dependencies {
    implementation(projects.shared)
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.multiplatform.material)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.pdfbox)
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "io.github.mrsimkin.dndcustomaid.desktop.MainKt"
    }
}


tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    systemProperty(
        "pcSheetProofDir",
        layout.buildDirectory.dir("pc-sheet-proofs").get().asFile.absolutePath,
    )
}
