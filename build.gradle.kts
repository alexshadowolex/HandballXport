import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "2.3.0"

    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("org.jetbrains.kotlin.plugin.compose") version kotlinVersion

    id("org.jetbrains.compose") version "1.7.0"
}

group = "alex.handball"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("org.slf4j:slf4j-simple:2.0.17")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

    implementation("com.github.tkuenneth:nativeparameterstoreaccess:0.1.3")

    implementation("org.jsoup:jsoup:1.23.2")

    implementation("org.apache.poi:poi:5.5.1")

    implementation("br.com.devsrsouza.compose.icons:tabler-icons:1.1.0")

    implementation(kotlin("script-runtime"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlin.sourceSets.all {
        languageSettings.apply {
            optIn("kotlin.RequiresOptIn")
            optIn("kotlin.time.ExperimentalTime")
        }
    }

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)

        freeCompilerArgs.addAll(
            "-Xcontext-receivers",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlin.time.ExperimentalTime"
        )
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "RefManager"
            packageVersion = version.toString()
        }
    }
}