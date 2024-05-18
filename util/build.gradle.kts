plugins {
    java
    id("buildlogic.java-conventions")
}

dependencies {
    // Fork of Spigot and SportBukkit running Minecraft 1.8
    compileOnly("app.ashcon:sportpaper:1.8.8-R0.1-SNAPSHOT")
}

sourceSets {
    main {
        resources {
            srcDirs(
                "src/main/resources", 
                "src/main/i18n/templates",
                "src/main/i18n/translations",
            )
        }
    }
}

description = "Util"