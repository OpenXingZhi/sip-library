import com.github.jengelman.gradle.plugins.shadow.ShadowExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `java-library`
    `maven-publish`
}

description = "Ceridwen's SIP Circulation Library for Android"

dependencies {
    implementation(project.rootProject)
    shadow(libs.champeau.openbeans)
}

tasks.withType<ShadowJar> {
    configurations = listOf(project.configurations.compileClasspath.get())
    dependencies {
        exclude(dependency("junit:junit"))
        exclude(dependency("org.hamcrest:hamcrest-core"))
        exclude(dependency("commons-net:commons-net"))
        exclude { dep -> dep.moduleGroup == "io.netty" }
    }
    relocate("java.beans", "com.googlecode.openbeans")
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