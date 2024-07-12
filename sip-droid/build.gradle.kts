import com.github.jengelman.gradle.plugins.shadow.ShadowExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.johnrengelman.shadowjar)
    `java-library`
    `maven-publish`
}

description = "Ceridwen's SIP Circulation Library for Android"

dependencies {
    implementation(project.rootProject)
    implementation(libs.champeau.openbeans)
    implementation(libs.com.ceridwen.util)
    shadow(libs.org.apache.commons.lang3)
    shadow(libs.commons.net)
    shadow(libs.commons.logging)
    shadow(libs.commons.beanutils)
}

tasks.withType<ShadowJar> {
    configurations = listOf(project.configurations.compileClasspath.get())
    dependencies {
        exclude(dependency(libs.org.apache.commons.lang3.get()))
        exclude(dependency(libs.commons.logging.get()))
        exclude(dependency(libs.commons.net.get()))
        exclude(dependency(libs.commons.beanutils.get()))
        exclude { dep -> dep.moduleGroup == "commons-collections" }
    }
    relocate("java.beans", "com.googlecode.openbeans")
    mergeServiceFiles()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/OpenXingZhi/sip-library")
            credentials(PasswordCredentials::class)
        }
    }
    publications {
        create<MavenPublication>("shadow") {
            project.extensions.getByType<ShadowExtension>().component(this)
        }
    }
}