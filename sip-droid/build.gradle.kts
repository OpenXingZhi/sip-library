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
    implementation(libs.champeau.openbeans)
}

tasks.shadowJar {
    from(rootProject.sourceSets.main.get().output)
    archiveClassifier = ""
    relocate("java.beans", "com.googlecode.openbeans")
    dependencies {
        exclude(dependency(".*:.*"))
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
