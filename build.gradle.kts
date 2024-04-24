plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.faendir"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("scripting-jvm-host"))
    implementation(kotlin("scripting-common"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

kotlin {
    jvmToolchain(8)
}

val uberJar = tasks.register<Jar>("uberJar") {
    group = "build"
    archiveClassifier = "uber"

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val prepareServe = tasks.register<Copy>("prepareServe") {
    group = "build"
    dependsOn(uberJar)
    from(uberJar.get().outputs)
    from("src/main/serve")
    into("build/serve")
}

tasks.register<Exec>("serve") {
    group = "serve"
    dependsOn(prepareServe)
    commandLine("npx", "http-server", "${layout.buildDirectory.get()}/serve/")
}