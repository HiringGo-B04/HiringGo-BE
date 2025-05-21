import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.johnrengelman.processes") version "0.5.0"
    id("org.sonarqube") version "4.4.1.3373"
}

sonar {
    properties {
        property("sonar.projectKey", "HiringGo-B04_HiringGo-BE")
        property("sonar.organization", "hiringgo-b04")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"

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
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<BootJar>().configureEach {
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
    runtimeOnly("org.postgresql:postgresql:42.7.2")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql") // still needed for prod
    runtimeOnly("com.h2database:h2") // ✅ this is the one you’re missing
    implementation("org.json:json:20210307") // Use the latest version
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    runtimeOnly("org.hibernate.orm:hibernate-core:6.3.1.Final")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumJavaVersion")
    testImplementation("io.github.bonigarcia:selenium-jupiter:$seleniumJupiterVersion")
    testImplementation("io.github.bonigarcia:webdrivermanager:$webdrivermanagerVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    // VALIDATION  ➜ jakarta.validation.Valid  (@NotBlank, @Min, dst.)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // SECURITY    ➜ PreAuthorize, EnableMethodSecurity, dll.
    implementation("org.springframework.boot:spring-boot-starter-security")

    testImplementation("org.springframework.security:spring-security-test")

    // MapStruct runtime (dipanggil saat konversi DTO ↔ entity)
    implementation("org.mapstruct:mapstruct:1.5.5.Final")

    // Annotation‑processor: Gradle akan men‑generate class mapper
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    testAnnotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    implementation("org.springframework.data:spring-data-commons")
}

// tasks.register<Test>("unitTest") {
//     description = "Runs unit tests."
//     group = "verification"

//     filter {
//     testImplementation("org.springframework.security:spring-security-test")
// }

tasks.test{
    systemProperty("spring.profiles.active", "test")

    filter{
        excludeTestsMatching("*FunctionalTest")
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport{
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.register<Test>("unitTest"){
    description = "Runs unit tests."
    group = "verification"

    filter{
        excludeTestsMatching("*FunctionalTest")
    }
}

tasks.register<Test> ("functionalTest") {
    description = "Runs the functional tests."
    group = "verification"

    filter {
        includeTestsMatching("*FunctionalTest")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.test {
    filter {
        excludeTestsMatching("*FunctionalTest")
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.withType<Test> {
    useJUnitPlatform()
}