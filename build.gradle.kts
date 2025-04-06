plugins {
    java
    scala
    id("io.gatling.gradle") version "3.9.2"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

val gatlingVersion = "3.9.2"
val scalaVersion = "2.13.12"


repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Java support Dependencies
    implementation("org.scala-lang:scala-library:$scalaVersion")
    implementation("io.gatling.highcharts:gatling-charts-highcharts:$gatlingVersion")
    implementation("io.gatling:gatling-app:$gatlingVersion")
    implementation("io.gatling:gatling-recorder:$gatlingVersion")

    // Scala support Dependencies
    testImplementation("org.scala-lang:scala-library:$scalaVersion")
    testImplementation("io.gatling.highcharts:gatling-charts-highcharts:$gatlingVersion")
    testImplementation("io.gatling:gatling-app:$gatlingVersion")
    testImplementation("io.gatling:gatling-recorder:$gatlingVersion")
}

sourceSets {
    named("main") {
        java.srcDirs("src/main/java")
        resources.srcDirs("src/main/resources")
    }
    named("test") {
        java.srcDirs("src/test/scala") // Gradle treats .scala and .java both as "java" sources
        resources.srcDirs("src/test/resources")
    }
}

// Task to run Java Gatling simulations
val runJavaGatling by tasks.registering(JavaExec::class) {
    group = "gatling"
    description = "Run Gatling simulations written in Java"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("simulations.JavaSimulation") // fully-qualified class name
}

/*
// Task to run Scala Gatling simulations (default gatling task)
// Custom Gatling task for Scala simulations
val pefTest by tasks.registering(JavaExec::class) {
    group = "gatling"
    description = "Run Scala Gatling performance test (pefTest)"

    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("io.gatling.app.Gatling")

    // Change simulationClass if needed, or leave to run interactively
    args = listOf(
            "-s", "com.gatling.simulation", // fully-qualified class name
            "-rf", "build/reports/pefTest"
    )
}
*/
tasks.register<JavaExec>("perfTest") {
    group = "verification"
    description = "Run Gatling performance tests"
    mainClass.set("io.gatling.app.Gatling")
    classpath = sourceSets["test"].runtimeClasspath
    args = listOf("-s", "com.gatling.simulation.Openbrewerydb") // Write the name of the Scala class which you want to execute

    jvmArgs = listOf("-Xms512M", "-Xmx2G")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<ScalaCompile> {
    scalaCompileOptions.encoding = "UTF-8"
}
// Avoid file copy conflicts during resource processing
tasks.withType<Copy> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
