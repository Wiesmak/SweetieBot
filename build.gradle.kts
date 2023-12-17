plugins {
    kotlin("jvm") version "1.9.21"
    application
    id("org.graalvm.buildtools.native") version "0.9.28"
}

group = "pl.bronieskrakow"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    repositories {
        maven {
            url = uri("https://raw.githubusercontent.com/graalvm/native-build-tools/snapshots")
        }
        gradlePluginPortal()
    }

    maven {
        name = "Sonatype Snapshots (Legacy)"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

    maven {
        name = "Sonatype Releases"
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kord.extensions)
    implementation(libs.slf4j)
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

application {
    mainClass.set("pl.bronieskrakow.sweetiebot.AppKt")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
