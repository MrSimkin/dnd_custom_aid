plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.shared)
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.multiplatform.material)
}

compose.desktop {
    application {
        mainClass = "io.github.mrsimkin.dndcustomaid.desktop.MainKt"
    }
}
