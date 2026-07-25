plugins {
    kotlin("jvm") version "2.0.21"
    application
}

group = "com.mnatorres.shortgoals"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.mnatorres.shortgoals.MainKt")
}

tasks.test {
    useJUnitPlatform()
}
