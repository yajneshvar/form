/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/6.8.2/userguide/building_java_projects.html
 */

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.4.20"

    // Apply the application plugin to add support for building a CLI application in Java.
    // application
}

repositories {
    // Use JCenter for resolving dependencies.
    jcenter()
}

val invoker by configurations.creating

dependencies {
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

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

//application {
//    // Define the main class for the application.
//    mainClass.set("forms.serverless.AppKt")
//}

task<JavaExec>("runFunction") {
    main = "com.google.cloud.functions.invoker.runner.Invoker"
    classpath(invoker)
    inputs.files(configurations.runtimeClasspath, sourceSets["main"].output)
    args(
            "--target", project.findProperty("runFunction.target") ?: "forms.serverless.FormFunction",
            "--port", project.findProperty("runFunction.port") ?: 8080
    )
    doFirst {
        args("--classpath", files(configurations.runtimeClasspath, sourceSets["main"].output).asPath)
    }
}
