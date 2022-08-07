plugins {
    id("java")
    id("eclipse")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.0.1"
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("org.sonarqube") version "3.3"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "dev.nikomaru"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
    maven("https://repo.incendo.org/content/repositories/snapshots")
}

val exposedVersion = "0.30.1"
val cloudVersion = "1.7.0"
dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.19-R0.1-SNAPSHOT")

    library(kotlin("stdlib"))

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")

    implementation("cloud.commandframework", "cloud-core", cloudVersion)
    implementation("cloud.commandframework", "cloud-kotlin-extensions", cloudVersion)
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-annotations", cloudVersion)
    implementation("cloud.commandframework", "cloud-kotlin-coroutines-annotations", cloudVersion)
    implementation("cloud.commandframework", "cloud-kotlin-coroutines", cloudVersion)

    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-java-time", exposedVersion)

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.2")

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.3.3")

    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-api", "2.2.0")
    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-core", "2.2.0")

    compileOnly ("com.github.Nlkomaru","NoticeItemAPI","v0.0.4")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.javaParameters = true
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    build {
        dependsOn(shadowJar)
    }
}

tasks {
    runServer {
        minecraftVersion("1.19.1")
    }
}


bukkit {
    name = "ObakeGiveItem"
    version = "miencraft_plugin_version"
    website = "https://github.com/Nlkomaru/ObakeGiveItem"

    main = "dev.nikomaru.obakegiveitem.ObakeGiveItem"

    depend= listOf("NoticeItem")
    apiVersion = "1.19"
    libraries = listOf("com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.2.0",
        "com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.2.0")
}