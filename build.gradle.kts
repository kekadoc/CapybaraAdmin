import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.20"
    id("org.jetbrains.compose")
}

group = "com.kekadoc.project.capybara.admin"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val ktorVersion = "2.3.0"
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.4.0")
                implementation("org.orbit-mvi:orbit-core:6.0.0")
                implementation("io.insert-koin:koin-core:3.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "com.kekadoc.project.capybara.admin.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "CapybaraAdmin"
            packageVersion = "1.0.0"
        }
    }
}