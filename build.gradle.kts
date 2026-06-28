plugins {
    java
    scala
    id("io.gatling.gradle") version "3.9.2"
}

group = "com.gatling"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val gatlingVersion = "3.9.2"
val scalaVersion = "2.13.12"

dependencies {

    // Scala Library (Required)
    implementation("org.scala-lang:scala-library:$scalaVersion")

    // Gatling
    gatlingImplementation("org.scala-lang:scala-library:$scalaVersion")
    gatlingImplementation("io.gatling:gatling-app:$gatlingVersion")
    gatlingImplementation("io.gatling.highcharts:gatling-charts-highcharts:$gatlingVersion")

    // Optional Recorder
    gatlingImplementation("io.gatling:gatling-recorder:$gatlingVersion")
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
            "com.gatling.simulation.Openbrewerydb"
    )

    jvmArgs("-Xms512m", "-Xmx2g")
}

tasks.withType<ScalaCompile>().configureEach {
    scalaCompileOptions.encoding = "UTF-8"
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Copy>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}