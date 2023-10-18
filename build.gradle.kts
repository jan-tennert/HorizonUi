import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import proguard.gradle.ProGuardTask

plugins {
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.serialization)
}

group = "io.github.jan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs) {
   //     exclude(group = "org.jetbrains.compose.material", module = "material")
    }
    implementation(compose.material3)
    implementation(libs.bundles.koin)
    implementation(libs.serialization)
    implementation(compose.materialIconsExtended)
    implementation(libs.datetime)
    val lwjglVersion = "3.3.1"
    listOf("lwjgl", "lwjgl-tinyfd").forEach { lwjglDep ->
        implementation("org.lwjgl:${lwjglDep}:${lwjglVersion}")
        listOf(
            "natives-windows",
            "natives-windows-x86",
            "natives-windows-arm64",
            "natives-macos",
            "natives-macos-arm64",
            "natives-linux",
            "natives-linux-arm64",
            "natives-linux-arm32"
        ).forEach { native ->
            runtimeOnly("org.lwjgl:${lwjglDep}:${lwjglVersion}:${native}")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "horizon-ui"
            packageVersion = "1.0.0"
            includeAllModules = true
        }

        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }
    }
}


tasks.register<ProGuardTask>("obfuscate") {
    val packageUberJarForCurrentOS by tasks.getting
    dependsOn(packageUberJarForCurrentOS)
    val files = packageUberJarForCurrentOS.outputs.files
    injars(files)
    outjars(files.map { file -> File(file.parentFile, "${file.nameWithoutExtension}.min.jar") })

    val library = if (System.getProperty("java.version").startsWith("1.")) "lib/rt.jar" else "jmods"
    libraryjars("${System.getProperty("java.home")}/$library")

    configuration(project.file("compose-desktop.pro"))
}