plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `java-library`
    `maven-publish`
}

group = "fr.izy"
version = "2.0.1"

repositories {
    mavenCentral()
}

dependencies {
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    testImplementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
}

(components["java"] as AdhocComponentWithVariants).withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "leaderos-api-wrapper"
            from(components["java"])

            pom {
                name.set("LeaderOS API Wrapper")
                description.set("Unofficial Kotlin wrapper of the LeaderOS API")
                url.set("https://github.com/iZyCorp/LeaderOS-Kotlin-API")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
