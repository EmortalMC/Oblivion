plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    java
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Minestom:Minestom:d6d1b85601")
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("Oblivion")
        mergeServiceFiles()
        minimize()
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
