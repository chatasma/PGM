plugins {
    java
    id("buildlogic.java-conventions")
}

dependencies {
    implementation(project(":util"))
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
}

description = "PlatformBukkitV1_20"
