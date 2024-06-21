plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false

    // Añadir la dependencia para el plugin de Google services
    id("com.google.gms.google-services") version "4.4.2" apply false

}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        // No añadas más repositorios aquí
    }
}
