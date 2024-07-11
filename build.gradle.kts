/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.junit.junit)
    api(libs.com.ceridwen.util.ceridwen.util)
    api(libs.org.apache.commons.commons.lang3)
    api(libs.commons.net.commons.net)
    api(libs.commons.logging.commons.logging)
    api(libs.commons.beanutils.commons.beanutils)
    api(libs.io.netty.netty.transport)
    api(libs.io.netty.netty.handler)
    api(libs.org.bouncycastle.bcpkix.jdk18on)
}

group = "com.ceridwen.circulation"
version = "2.11.2"
description = "com.ceridwen.circulation:ceridwen-standard-interchange-protocol-library"
java.sourceCompatibility = JavaVersion.VERSION_1_8

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
