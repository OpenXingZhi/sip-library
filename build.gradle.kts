plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    implementation(libs.junit)
    implementation(libs.com.ceridwen.util)
    implementation(libs.org.apache.commons.commons.lang3)
    implementation(libs.commons.net)
    implementation(libs.commons.logging)
    implementation(libs.commons.beanutils)
    implementation(libs.io.netty.transport)
    implementation(libs.io.netty.handler)
    implementation(libs.org.bouncycastle.bcpkix.jdk18on)
}

allprojects {
    group = "com.xingzhi.circulation"
    version = "2.11.4"
}
description = "Ceridwen's SIP Circulation Library for Java"

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
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
