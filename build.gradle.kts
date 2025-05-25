import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.johnrengelman.processes") version "0.5.0"
    id("org.sonarqube") version "4.4.1.3373" // or latest version
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"

sonarqube {
    properties {
        property("sonar.projectKey", "HiringGo-B04_HiringGo-BE")
        property("sonar.organization", "hiringgo-b04")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("io.github.cdimascio:dotenv-kotlin:6.4.1")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<BootJar> {
    destinationDirectory.set(layout.buildDirectory.dir("libs"))
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val seleniumJavaVersion = "4.14.1"
val seleniumJupiterVersion = "5.0.1"
val webdrivermanagerVersion = "5.6.3"
val junitJupiterVersion = "5.9.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    runtimeOnly("org.postgresql:postgresql:42.7.2")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.hibernate.orm:hibernate-core:6.3.1.Final")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    compileOnly("org.projectlombok:lombok")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.json:json:20210307")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.springframework.data:spring-data-commons")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumJavaVersion")
    testImplementation("io.github.bonigarcia:selenium-jupiter:$seleniumJupiterVersion")
    testImplementation("io.github.bonigarcia:webdrivermanager:$webdrivermanagerVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    testImplementation("org.springframework.security:spring-security-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
}

// Ensure all test tasks use JUnit Platform
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperty("spring.profiles.active", "test")
}

// Separate unit test and functional test tasks
tasks.register<Test>("unitTest") {
    description = "Runs unit tests."
    group = "verification"
    useJUnitPlatform()
    filter {
        excludeTestsMatching("*FunctionalTest")
    }
}

tasks.register<Test>("functionalTest") {
    description = "Runs the functional tests."
    group = "verification"
    useJUnitPlatform()
    filter {
        includeTestsMatching("*FunctionalTest")
    }
}

// JaCoCo configuration
jacoco {
    toolVersion = "0.8.11" // or latest stable version
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // Ensures test runs before report
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    classDirectories.setFrom(
        fileTree(layout.buildDirectory.dir("classes/java/main")) {
            exclude("**/dto/**", "**/config/**", "**/exception/**") // example
        }
    )
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(fileTree(buildDir).include("/jacoco/test.exec"))
}

tasks.test {
    filter {
        excludeTestsMatching("*FunctionalTest")
    }
    finalizedBy(tasks.jacocoTestReport) // Auto-generate report after test
}
