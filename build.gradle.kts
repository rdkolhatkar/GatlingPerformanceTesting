plugins {
    java
    scala
    id("io.gatling.gradle") version "3.10.0"
}

group = "com.gatling"
version = "3.10.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val gatlingVersion = "3.10.0"
val scalaVersion = "2.13.12"

dependencies {

    // Scala
    implementation("org.scala-lang:scala-library:$scalaVersion")

    // Gatling
    gatlingImplementation("org.scala-lang:scala-library:$scalaVersion")
    gatlingImplementation("io.gatling:gatling-app:$gatlingVersion")
    gatlingImplementation("io.gatling.highcharts:gatling-charts-highcharts:$gatlingVersion")

    // Gatling Recorder
    gatlingImplementation("io.gatling:gatling-recorder:$gatlingVersion")

    // Required so Engine.scala can access Gatling classes
    testImplementation("io.gatling:gatling-app:$gatlingVersion")
    testImplementation("io.gatling.highcharts:gatling-charts-highcharts:$gatlingVersion")
    testImplementation("org.scala-lang:scala-library:$scalaVersion")
}

sourceSets {
    named("gatling") {
        scala.srcDir("src/test/scala")
        resources.srcDir("src/test/resources")
    }
}

tasks.register<JavaExec>("perfTest") {
    group = "verification"
    description = "Run Gatling Performance Tests"

    classpath = sourceSets["gatling"].runtimeClasspath

    mainClass.set("io.gatling.app.Gatling")

    args = listOf(
            "-s",
            "com.gatling.simulation.ComputerDatabaseSimulation"
    )

    jvmArgs(
            "-Xms512m",
            "-Xmx2g"
    )
}

tasks.withType<ScalaCompile>().configureEach {

    scalaCompileOptions.encoding = "UTF-8"

    scalaCompileOptions.additionalParameters = listOf(
            "-deprecation",
            "-feature",
            "-unchecked",
            "-language:implicitConversions",
            "-language:postfixOps"
    )
}

tasks.withType<ProcessResources>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}