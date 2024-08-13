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
    implementation("org.scala-lang:scala-library:$scalaVersion")
    implementation("io.gatling.highcharts:gatling-charts-highcharts:$gatlingVersion")
    implementation("io.gatling:gatling-app:$gatlingVersion")
    implementation("io.gatling:gatling-recorder:$gatlingVersion")
}

sourceSets {
    test {
        scala {
            setSrcDirs(listOf("src/test/scala"))
        }
        resources {
            setSrcDirs(listOf("src/test/resources"))
        }
    }
}

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
