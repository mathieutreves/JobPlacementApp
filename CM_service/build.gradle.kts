plugins {
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.6"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "com.example"
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
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.apache.camel.springboot:camel-dataformat-starter:4.6.0")
	implementation("org.apache.camel.springboot:camel-jackson-starter:4.6.0")
	implementation("org.apache.camel.springboot:camel-spring-boot-starter:4.6.0")
	implementation("org.apache.camel.springboot:camel-google-mail-starter:4.6.0")
	implementation("org.apache.camel:camel-google-mail:4.6.0")
	implementation("org.apache.camel:camel-http:4.6.0")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
	implementation("org.apache.httpcomponents.core5:httpcore5:5.2.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.apache.camel.springboot:camel-mock-starter:4.6.0")
	testImplementation("org.apache.camel:camel-test-spring-junit5:4.6.0")
	testImplementation("org.mockito:mockito-core:3.12.4")

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

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
	imageName.set("com.example/cm_service:0.0.1-snapshot")
}
