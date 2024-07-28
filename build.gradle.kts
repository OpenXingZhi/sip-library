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
    runtimeOnly(libs.io.netty.transport)
    implementation(libs.io.netty.handler)
    runtimeOnly(libs.org.bouncycastle.bcpkix.jdk18on)
    shadow(libs.champeau.openbeans)
}

group = "com.xingzhi.circulation"
version = "2.11.11"
description = "Ceridwen's SIP Circulation Library for Java"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
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
    relocate("org.apache", "shaded.org.apache")
    configurations = listOf(project.configurations.compileClasspath.get())
    dependencies {
        // Junit
        exclude(dependency(libs.junit.get()))
        exclude(dependency("org.hamcrest:hamcrest-core"))
        // Already shadow dependency
        exclude(dependency(libs.champeau.openbeans.get()))
        // Netty
        exclude { it.moduleGroup == "io.netty" }
    }
    mergeServiceFiles()
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