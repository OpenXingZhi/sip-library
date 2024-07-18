plugins {
    alias(libs.plugins.johnrengelman.shadowjar)
    `java-library`
    `maven-publish`
}

dependencies {
    implementation(libs.junit)
    implementation(libs.com.ceridwen.util)
    implementation(libs.org.apache.commons.lang3)
    implementation(libs.commons.net)
    implementation(libs.commons.logging)
    implementation(libs.commons.beanutils)
    implementation(libs.io.netty.transport)
    implementation(libs.io.netty.handler)
    runtimeOnly(libs.org.bouncycastle.bcpkix.jdk18on)
    shadow(libs.champeau.openbeans)
}

group = "com.xingzhi.circulation"
version = "2.11.6"
description = "Ceridwen's SIP Circulation Library for Java"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

configurations {
    shadowRuntimeElements {
        outgoing {
            capability("com.xingzhi.circulation:sip-droid:$version")
        }
    }
}

tasks.shadowJar {
    archiveClassifier = "droid"
    relocate("java.beans", "com.googlecode.openbeans")
    configurations = listOf(project.configurations.compileClasspath.get())
    mergeServiceFiles()
    minimize()
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
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}