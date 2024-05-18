import java.io.ByteArrayOutputStream

plugins {
    java
    id("buildlogic.java-conventions")
    id("com.gorylenko.gradle-git-properties") version "2.4.2"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

description = "The original PvP Game Manager for Minecraft."
val projectUrl = "https://pgm.dev/"
val mainClass = "tc.oc.pgm.PGMPlugin"
val commitHash = getGitHash()

dependencies {
    implementation(project(":util"))
    implementation(project(":platform-target"))

    // Fork of Spigot and SportBukkit running Minecraft 1.8
    compileOnly("app.ashcon:sportpaper:1.8.8-R0.1-SNAPSHOT")
}

// git.properties
gitProperties {
    failOnNoGitDirectory = false
}

tasks {
    // src/main/resources variable expansions
    processResources {
        expand(
            "mainClass" to mainClass, 
            "url" to projectUrl,
            "description" to description,
            "version" to version,
            "commit" to commitHash
        )
    }

    shadowJar {
        // JAR name
        val archiveName = "PGM-${archiveVersion.get()}-${commitHash}.jar" 
        archiveFileName.set(archiveName)

        // Relocations
        val relocations = mapOf(
            "cloud.commandframework" to "tc.oc.pgm.lib.cloud.commandframework",
            "io.leangen.geantyref" to "tc.oc.pgm.lib.io.leangen.geantyref",
            "me.lucko.commodore" to "tc.oc.pgm.lib.me.lucko.commodore",
            "fr.mrmicky" to "tc.oc.pgm.lib.fr.mrmicky",
            "org.jdom2" to "tc.oc.pgm.lib.org.jdom2",
            "org.eclipse.jgit" to "tc.oc.pgm.lib.org.eclipse.jgit",
            "org.slf4j" to "tc.oc.pgm.lib.org.slf4j",
            "com.cryptomorin.xseries" to "tc.oc.pgm.lib.com.cryptomorin.xseries"
        )
        relocations.forEach { (src, dst) -> relocate(src, dst) }
    }
}

fun getGitHash() : String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine(listOf("git", "rev-parse", "--short", "HEAD"))
        standardOutput = stdout
    }
    return stdout.toString().trim()
}
