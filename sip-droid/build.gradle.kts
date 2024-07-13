plugins {
    alias(libs.plugins.johnrengelman.shadowjar)
    `java-library`
    `maven-publish`
}

description = "Ceridwen's SIP Circulation Library for Android"

configurations {
    implementation.get().extendsFrom(rootProject.configurations.implementation.get())
}

dependencies {
    shadow(rootProject)
    implementation(libs.champeau.openbeans)
}

tasks.shadowJar {
    archiveClassifier = ""
    relocate("java.beans", "com.googlecode.openbeans")
    configurations = listOf(project.configurations.compileClasspath.get())
    dependencies {
        exclude(dependency(libs.champeau.openbeans.get()))
    }
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
            from(components["java"])
            artifacts.clear()
            artifact(tasks.shadowJar) {
                classifier = ""
            }
        }
    }
}
