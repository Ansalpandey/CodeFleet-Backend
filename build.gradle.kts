plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("org.springframework.boot") version "3.4.2"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "com.app.cloudide"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-websocket")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.json:json:20250107")
  implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
  implementation("io.jsonwebtoken:jjwt-api:0.12.6")
  implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
  implementation("org.jetbrains.pty4j:pty4j:0.13.2")
  implementation("org.apache.commons:commons-exec:1.3")
  implementation("commons-io:commons-io:2.15.1")
  implementation("com.github.docker-java:docker-java:3.3.0")
  implementation("com.github.docker-java:docker-java-transport-okhttp:3.2.14")
  implementation("javax.ws.rs:javax.ws.rs-api:2.1.1")
  implementation("org.glassfish.jersey.core:jersey-client:2.35")
  implementation("org.glassfish.jersey.inject:jersey-hk2:2.35")
  implementation("jakarta.validation:jakarta.validation-api:3.0.2") // Validation API
  implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final") // Hibernate Validator implementation
  implementation("org.glassfish:jakarta.el:4.0.2") // Expression Language dependency required by Hibernate Validator
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.springframework.security:spring-security-test")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
