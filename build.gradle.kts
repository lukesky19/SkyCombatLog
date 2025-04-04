plugins {
    java
}

group = "com.github.lukesky19"
version = "1.0.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.lukesky19:SkyLib:1.2.0.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<ProcessResources> {
    val props = mapOf("version" to version)

    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.jar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
    archiveClassifier.set("")
}