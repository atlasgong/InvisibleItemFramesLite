plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    api(libs.bstats.bukkit)
    compileOnly(libs.paper.api)
    compileOnly(libs.adventure.legacy)

    testImplementation(libs.paper.api)
    testImplementation(libs.adventure.legacy)
    testImplementation(libs.mockbukkit)
    testImplementation(enforcedPlatform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

group = "com.atlasgong"
version = "3.2.3-SNAPSHOT"
description = "InvisibleItemFramesLite"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}
