plugins {
    java
    id("buildlogic.java-conventions")
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

dependencies {
    implementation(project(":util"))
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
}

description = "PlatformBukkitV1_20"
