plugins {
    kotlin("jvm") version "1.9.21"
    application
}

group = "pl.bronieskrakow"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()

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