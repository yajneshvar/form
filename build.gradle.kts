import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.8.2/userguide/building_java_projects.html
 */

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.4.32"
    id("org.jetbrains.kotlin.kapt") version "1.4.32"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.micronaut.application") version "1.5.4"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.32"
    // Apply the application plugin to add support for building a CLI application in Java.
    // application
}

val compileKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions.jvmTarget = "1.8"

repositories {
    // Use JCenter for resolving dependencies.
    mavenCentral()
}

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("forms.*")
    }
}

application {
    mainClass.set("forms.ApplicationKt")
}

val kotlinVersion=project.properties.get("kotlinVersion")
val invoker by configurations.creating

dependencies {

    kapt("io.micronaut.openapi:micronaut-openapi")
//    kapt("io.micronaut.security:micronaut-security-annotations")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
//    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("javax.annotation:javax.annotation-api")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    runtimeOnly("org.apache.logging.log4j:log4j-api:2.14.1")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
    implementation("io.micronaut:micronaut-validation")

    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.google.apis:google-api-services-sheets:v4-rev581-1.25.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:0.23.0")

    //implementation("javax.mail:javax.mail-api:1.6.2")
    // https://mvnrepository.com/artifact/com.sun.mail/javax.mail
    implementation("com.sun.mail:javax.mail:1.6.2")

    implementation("com.google.apis:google-api-services-gmail:v1-rev20210111-1.31.0")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:29.0-jre")


    // Every function needs this dependency to get the Functions Framework API.
    compileOnly("com.google.cloud.functions:functions-framework-api:1.0.1")

    // To run function locally using Functions Framework's local invoker
    invoker("com.google.cloud.functions.invoker:java-function-invoker:1.0.0-alpha-2-rc5")

    // https://mvnrepository.com/artifact/com.google.cloud/google-cloud-firestore
    implementation("com.google.cloud:google-cloud-firestore:2.6.2")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    implementation( "com.google.api-client:google-api-client:1.31.2")
}

//task<JavaExec>("runFunction") {
//    main = "com.google.cloud.functions.invoker.runner.Invoker"
//    classpath(invoker)
//    inputs.files(configurations.runtimeClasspath, sourceSets["main"].output)
//    args(
//            "--target", project.findProperty("runFunction.target") ?: "forms.FormFunction",
//            "--port", project.findProperty("runFunction.port") ?: 8080
//    )
//    doFirst {
//        args("--classpath", files(configurations.runtimeClasspath, sourceSets["main"].output).asPath)
//    }
//}
