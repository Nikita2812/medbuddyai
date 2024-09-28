// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
    }
}

plugins {
    id("com.android.application") version "8.6.0" apply false
    id("com.android.library") version "7.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}