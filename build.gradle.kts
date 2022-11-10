plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Minestom:Minestom:1a013728fd")
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("Oblivion")
        mergeServiceFiles()
        manifest {
            attributes["Main-Class"] = "dev.emortal.oblivion.OblivionMain"
        }
    }

    build { dependsOn(shadowJar) }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
