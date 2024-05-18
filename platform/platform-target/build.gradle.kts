plugins {
    java
    id("buildlogic.java-conventions")
}

dependencies {
    implementation(project(":platform-v1-13"))
    implementation(project(":platform-v1-20"))
}

description = "platform-target"
