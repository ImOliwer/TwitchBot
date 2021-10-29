plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "xyz.oliwer"
version = "0.0.1-B"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.12.3")
    implementation(group = "com.github.twitch4j", name = "twitch4j", version = "1.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

tasks.test {
    useJUnitPlatform()
}