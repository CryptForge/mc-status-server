plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

group = "me.cryptforge"
version = "1.0"

val adventureVersion = "4.11.0"

repositories {
    mavenCentral()
}

dependencies {
    // Json
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")

    // Minecraft Text
    implementation("net.kyori:adventure-api:$adventureVersion")
    implementation("net.kyori:adventure-text-serializer-gson:$adventureVersion")
    implementation("net.kyori:adventure-text-minimessage:$adventureVersion")

    // Config
    implementation("org.spongepowered:configurate-hocon:4.1.2")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.3")
    implementation("ch.qos.logback:logback-classic:1.4.4")
}

tasks.shadowJar {
    manifest {
        attributes(
            "Main-Class" to "me.cryptforge.Main"
        )
    }
}