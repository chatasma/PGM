plugins {
    java
    id("buildlogic.java-conventions")
}

dependencies {
    implementation(project(":util"))
    compileOnly("org.spigotmc:spigot-api:1.13-R0.1-SNAPSHOT")
}

description = "PlatformBukkitV1_13"